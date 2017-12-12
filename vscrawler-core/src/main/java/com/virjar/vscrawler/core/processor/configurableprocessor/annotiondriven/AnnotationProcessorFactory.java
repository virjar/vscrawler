package com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven;

import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.processor.RouteProcessor;
import com.virjar.vscrawler.core.processor.SeedProcessor;
import com.virjar.vscrawler.core.util.ClassScanner;

import java.util.List;

/**
 * Created by virjar on 2017/12/12.<br/>
 * 加载所有的的基于注解的爬虫
 *
 * @author virjar
 * @since 0.2.1
 */
public class AnnotationProcessorFactory {
    private List<String> scanPackage = Lists.newLinkedList();
    private List<AnnotationProcessor<? extends AbstractAutoProcessModel>> annotationProcessors = Lists.newLinkedList();
    private List<AbstractAutoProcessModel> allModels = Lists.newLinkedList();

    private void scan() {
        ClassScanner.SubClassVisitor<AbstractAutoProcessModel> subClassVisitor = new ClassScanner.SubClassVisitor<>(true, AbstractAutoProcessModel.class);
        ClassScanner.scan(subClassVisitor, scanPackage);
        List<Class<? extends AbstractAutoProcessModel>> subClass = subClassVisitor.getSubClass();

    }

    private SeedProcessor build() {
        RouteProcessor routeProcessor = new RouteProcessor();
        for (AnnotationProcessor annotationProcessor : annotationProcessors) {
            routeProcessor.addRouter(annotationProcessor);
        }
        return routeProcessor;
    }
}
