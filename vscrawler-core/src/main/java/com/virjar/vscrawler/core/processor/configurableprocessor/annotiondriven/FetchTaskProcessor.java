package com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven;

import com.google.common.collect.Lists;
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
public class FetchTaskProcessor<T extends AbstractAutoProcessModel> {
    private List<FetchTaskBean> fetchTaskBeanList = Lists.newLinkedList();
    @NonNull
    private AnnotationProcessorFactory annotationProcessorFactory;

    public void registerTask(FetchTaskBean fetchTaskBean) {
        fetchTaskBeanList.add(fetchTaskBean);
    }

    public List<Seed> injectField(T model, AbstractSelectable abstractSelectable) {
        List<Seed> newSeeds = Lists.newLinkedList();
        try {
            for (FetchTaskBean fetchTaskBean : fetchTaskBeanList) {
                Object data = fetchTaskBean.getModelSelector().select(abstractSelectable).createOrGetModel();
                if (data == null) {
                    continue;
                }
                Field field = fetchTaskBean.getField();
                //基本类型,字符串,json可以直接设置值
                if (field.getType().isAssignableFrom(data.getClass())) {
                    field.set(model, data);
                    continue;
                }
                //TODO
                //if(field.getType().isAssignableFrom())
            }
        } catch (Exception e) {
            throw new RuntimeException("can not inject data for model:" + model.getClass().getName(), e);
        }
        return newSeeds;
    }
}
