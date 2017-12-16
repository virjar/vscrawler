package com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.TypeUtils;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.virjar.sipsoup.model.SIPNode;
import com.virjar.sipsoup.util.ObjectFactory;
import com.virjar.vscrawler.core.processor.CrawlResult;
import com.virjar.vscrawler.core.seed.Seed;
import com.virjar.vscrawler.core.selector.combine.AbstractSelectable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by virjar on 2017/12/10.<br/>
 * covert data to field and inject to model
 * *
 *
 * @author virjar
 * @since 0.2.1
 */
@RequiredArgsConstructor
class FetchTaskProcessor {
    private List<FetchTaskBean> fetchTaskBeanList = Lists.newLinkedList();
    @NonNull
    private AnnotationProcessorBuilder annotationProcessorBuilder;

    void registerTask(FetchTaskBean fetchTaskBean) {
        fetchTaskBeanList.add(fetchTaskBean);
    }


    private Object unPackSipNode(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof SIPNode) {
            return handleSingleSipNode((SIPNode) object);
        }
        if (object instanceof Collection) {
            return Collections2.transform((Collection) object, new Function<Object, Object>() {
                @Override
                public Object apply(Object input) {
                    if (!(input instanceof SIPNode)) {
                        return input;
                    }
                    return handleSingleSipNode((SIPNode) input);
                }
            });
        }
        return object;
    }

    private Object handleSingleSipNode(SIPNode sipNode) {
        if (sipNode.isText()) {
            return sipNode.getTextVal();
        }
        return sipNode.getElement();
    }

    private Object unpackCollection(Class type, Object data) {
        if (data == null) {
            return null;
        }
        if (data instanceof Iterable && !Iterable.class.isAssignableFrom(type)) {
            Iterator iterator = ((Iterable) data).iterator();
            if (!iterator.hasNext()) {
                return null;
            }
            Object newData = iterator.next();
            if (iterator.hasNext()) {
                throw new IllegalStateException("can not transfer " + data.getClass() + " to " + type + " for set multi data to single model");
            }
            return newData;
        } else if (data instanceof Array && !type.isArray()) {
            int length = Array.getLength(data);
            if (length <= 0) {
                return null;
            }
            if (length > 1) {
                throw new IllegalStateException("can not transfer " + data.getClass() + " to " + type + " for set multi data to single model");
            }
            return Array.get(data, 0);
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    private AbstractAutoProcessModel fetchSubModel(Class type, AbstractSelectable subSelectable, AbstractAutoProcessModel parentModel, CrawlResult crawlResult) {
        AbstractAutoProcessModel subModel = (AbstractAutoProcessModel) ObjectFactory.newInstance(type);
        subModel.setBaseUrl(parentModel.getBaseUrl());
        subModel.setSeed(parentModel.seed);
        subModel.setOriginSelectable(subSelectable);
        String rawText = subSelectable.getRawText();
        subModel.setRawText(rawText);
        subModel.beforeAutoFetch();
        annotationProcessorBuilder.findExtractor((Class<? extends AbstractAutoProcessModel>) type).process(parentModel.seed, rawText, crawlResult, subModel, subSelectable, false);
        subModel.afterAutoFetch();
        return subModel;
    }


    @SuppressWarnings("unchecked")
    List<Seed> injectField(final AbstractAutoProcessModel model, AbstractSelectable abstractSelectable, final CrawlResult crawlResult, boolean save) {
        List<Seed> newSeeds = Lists.newLinkedList();

        for (final FetchTaskBean fetchTaskBean : fetchTaskBeanList) {
            try {
                Field field = fetchTaskBean.getField();
                final Class<?> type = field.getType();
                //处理循环的model,支持子结构抽取
                if (AbstractAutoProcessModel.class.isAssignableFrom(type) && annotationProcessorBuilder.findExtractor((Class<? extends AbstractAutoProcessModel>) type) != null) {
                    AbstractSelectable subSelectable = fetchTaskBean.getModelSelector().select(abstractSelectable);
                    field.set(model, fetchSubModel(type, subSelectable, model, crawlResult));
                    continue;
                }

                Object data = null;

                if (Collection.class.isAssignableFrom(type) && fetchTaskBean.getHelpClazz() != Object.class && annotationProcessorBuilder.findExtractor(fetchTaskBean.getHelpClazz()) != null) {
                    List<AbstractSelectable> abstractSelectables = fetchTaskBean.getModelSelector().select(abstractSelectable).toMultiSelectable();
                    data = Lists.transform(abstractSelectables, new Function<AbstractSelectable, AbstractAutoProcessModel>() {
                        @Override
                        public AbstractAutoProcessModel apply(AbstractSelectable input) {
                            return fetchSubModel(fetchTaskBean.getHelpClazz(), input, model, crawlResult);
                        }
                    });
                }

                //非子model抽取,需要直接抽取到结果,结束抽取链,判断抽取结果类型,进行数据类型转换操作
                if (data == null) {
                    data = fetchTaskBean.getModelSelector().select(abstractSelectable).createOrGetModel();
                }

                //特殊逻辑,因为SipNode对象同时持有字符串或者dom对象,所以需要对他进行拆箱
                data = unPackSipNode(data);
                //如果目标类型不是集合或者数组,且源数据为集合,则进行集合拆箱
                data = unpackCollection(type, data);
                if (data == null) {
                    continue;
                }

                Object transformedObject = TypeCastUtils.cast(data, type);
                if (transformedObject == null) {
                    transformedObject = TypeUtils.cast(data, type, ParserConfig.getGlobalInstance());
                }
                field.set(model, transformedObject);

                if (fetchTaskBean.isNewSeed()) {
                    //新种子注入处理
                    if (transformedObject instanceof String) {
                        Seed seed = new Seed(transformedObject.toString());
                        seed.getExt().put("fromUrl", model.getBaseUrl());
                        newSeeds.add(seed);
                    } else if (transformedObject instanceof Seed) {
                        Seed seed = (Seed) transformedObject;
                        seed.getExt().put("fromUrl", model.getBaseUrl());
                        crawlResult.addSeed(seed);
                    } else if (transformedObject instanceof Collection) {
                        int size = ((Collection) transformedObject).size();
                        if (size <= 0) {
                            continue;
                        }
                        Object next = ((Collection) transformedObject).iterator().next();
                        if (next instanceof String) {
                            crawlResult.addSeeds(Collections2.transform((Collection<Object>) transformedObject, new Function<Object, Seed>() {
                                @Override
                                public Seed apply(Object input) {
                                    Seed seed = new Seed(input.toString());
                                    seed.getExt().put("fromUrl", model.getBaseUrl());
                                    return seed;
                                }
                            }));
                        } else if (next instanceof Seed) {
                            crawlResult.addSeeds(Collections2.transform((Collection<Object>) transformedObject, new Function<Object, Seed>() {
                                @Override
                                public Seed apply(Object input) {
                                    Seed seed = (Seed) input;
                                    seed.getExt().put("fromUrl", model.getBaseUrl());
                                    return seed;
                                }
                            }));
                        } else {
                            throw new IllegalStateException("unknown type for " + next.getClass().getName() + " to transfer to new Seed");
                        }
                    } else {
                        throw new IllegalStateException("unknown type for " + transformedObject.getClass().getName() + " to transfer to new Seed");
                    }

                }
            } catch (Exception e) {
                throw new RuntimeException("can not inject data for model:" + model.getClass().getName() + " for field: " + fetchTaskBean.getField().getName(), e);
            }
        }

        return newSeeds;
    }
}
