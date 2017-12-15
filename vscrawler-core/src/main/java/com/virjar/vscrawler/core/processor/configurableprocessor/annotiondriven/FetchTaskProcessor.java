package com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven;

import com.google.common.collect.Lists;
import com.virjar.sipsoup.util.ObjectFactory;
import com.virjar.vscrawler.core.processor.CrawlResult;
import com.virjar.vscrawler.core.seed.Seed;
import com.virjar.vscrawler.core.selector.combine.AbstractSelectable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
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
    public List<Seed> injectField(AbstractAutoProcessModel model, AbstractSelectable abstractSelectable, CrawlResult crawlResult) {
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
                    annotationProcessorFactory.findExtractor((Class<? extends AbstractAutoProcessModel>) type).process(model.seed, rawText, crawlResult, subModel);
                    subModel.afterAutoFetch();
                    field.set(model, subModel);
                    continue;
                }
                //非子model抽取,需要直接抽取到结果,结束抽取链,判断抽取结果类型,进行数据类型转换操作
                Object data = fetchTaskBean.getModelSelector().select(abstractSelectable).createOrGetModel();
                if (data == null) {
                    continue;
                }
                //基本类型,字符串,json可以直接设置值
                if (field.getType().isAssignableFrom(data.getClass())) {
                    field.set(model, data);
                    continue;
                }
                //TODO

            }
        } catch (Exception e) {
            throw new RuntimeException("can not inject data for model:" + model.getClass().getName(), e);
        }
        return newSeeds;
    }
}
