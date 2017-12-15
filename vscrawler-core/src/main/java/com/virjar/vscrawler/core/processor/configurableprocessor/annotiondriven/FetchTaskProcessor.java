package com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.TypeUtils;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
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
public class FetchTaskProcessor {
    private List<FetchTaskBean> fetchTaskBeanList = Lists.newLinkedList();
    @NonNull
    private AnnotationProcessorFactory annotationProcessorFactory;

    public void registerTask(FetchTaskBean fetchTaskBean) {
        fetchTaskBeanList.add(fetchTaskBean);
    }

    @SuppressWarnings("unchecked")
    public List<Seed> injectField(AbstractAutoProcessModel model, AbstractSelectable abstractSelectable, CrawlResult crawlResult, boolean save) {
        List<Seed> newSeeds = Lists.newLinkedList();
        try {
            for (FetchTaskBean fetchTaskBean : fetchTaskBeanList) {
                Field field = fetchTaskBean.getField();
                Class<?> type = field.getType();
                //处理循环的model,支持子结构抽取
                if (AbstractAutoProcessModel.class.isAssignableFrom(type) && annotationProcessorFactory.findExtractor((Class<? extends AbstractAutoProcessModel>) type) != null) {
                    AbstractSelectable subSelectable = fetchTaskBean.getModelSelector().select(abstractSelectable);
                    AbstractAutoProcessModel subModel = (AbstractAutoProcessModel) ObjectFactory.newInstance(type);
                    subModel.setBaseUrl(model.getBaseUrl());
                    subModel.setSeed(model.seed);
                    subModel.setOriginSelectable(subSelectable);
                    String rawText = subSelectable.getRawText();
                    subModel.setRawText(rawText);
                    subModel.beforeAutoFetch();
                    annotationProcessorFactory.findExtractor((Class<? extends AbstractAutoProcessModel>) type).process(model.seed, rawText, crawlResult, subModel, subSelectable, save);
                    subModel.afterAutoFetch();
                    field.set(model, subModel);
                    continue;
                }
                //非子model抽取,需要直接抽取到结果,结束抽取链,判断抽取结果类型,进行数据类型转换操作
                Object data = fetchTaskBean.getModelSelector().select(abstractSelectable).createOrGetModel();
                if (data == null) {
                    continue;
                }
                if (data instanceof Iterable && !Iterable.class.isAssignableFrom(type)) {
                    Iterator iterator = ((Iterable) data).iterator();
                    if (!iterator.hasNext()) {
                        continue;
                    }
                    Object newData = iterator.next();
                    if (iterator.hasNext()) {
                        throw new IllegalStateException("can not transfer " + data.getClass() + " to " + type + " for set multi data to single model");
                    }
                    data = newData;
                } else if (data instanceof Array && !type.isArray()) {
                    int length = Array.getLength(data);
                    if (length <= 0) {
                        continue;
                    }
                    if (length > 1) {
                        throw new IllegalStateException("can not transfer " + data.getClass() + " to " + type + " for set multi data to single model");
                    }
                    data = Array.get(data, 0);
                }

                Object transformedObject = TypeUtils.cast(data, type, ParserConfig.getGlobalInstance());
                field.set(model, transformedObject);

                if (fetchTaskBean.isNewSeed()) {
                    //新种子注入处理
                    if (transformedObject instanceof String) {
                        crawlResult.addSeed(transformedObject.toString());
                    } else if (transformedObject instanceof Seed) {
                        crawlResult.addSeed((Seed) transformedObject);
                    } else if (transformedObject instanceof Collection) {
                        int size = ((Collection) transformedObject).size();
                        if (size <= 0) {
                            continue;
                        }
                        Object next = ((Collection) transformedObject).iterator().next();
                        if (next instanceof String) {
                            crawlResult.addStrSeeds(Collections2.transform((Collection<? extends Object>) transformedObject, new Function<Object, String>() {
                                @Override
                                public String apply(Object input) {
                                    return input.toString();
                                }
                            }));
                        } else if (next instanceof Seed) {
                            crawlResult.addSeeds(Collections2.transform((Collection<Object>) transformedObject, new Function<Object, Seed>() {
                                @Override
                                public Seed apply(Object input) {
                                    return (Seed) input;
                                }
                            }));
                        } else {
                            throw new IllegalStateException("unknown type for " + next.getClass().getName() + " to transfer to new Seed");
                        }
                    } else {
                        throw new IllegalStateException("unknown type for " + transformedObject.getClass().getName() + " to transfer to new Seed");
                    }

                }
            }
        } catch (Exception e) {
            throw new RuntimeException("can not inject data for model:" + model.getClass().getName(), e);
        }
        return newSeeds;
    }
}
