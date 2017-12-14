package com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.virjar.vscrawler.core.processor.RouteProcessor;
import com.virjar.vscrawler.core.processor.SeedProcessor;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.annotation.AutoProcessor;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.annotation.MatchSeed;
import com.virjar.vscrawler.core.seed.Seed;
import com.virjar.vscrawler.core.util.ClassScanner;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by virjar on 2017/12/12.<br/>
 * 加载所有的的基于注解的爬虫
 *
 * @author virjar
 * @since 0.2.1
 */
public class AnnotationProcessorFactory {
    private List<String> scanPackage = Lists.newLinkedList();
    private List<AnnotationSeedProcessor<? extends AbstractAutoProcessModel>> annotationSeedProcessors = Lists.newLinkedList();
    private Map<Class<? extends AbstractAutoProcessModel>, ModelExtractor> allExtractors = Maps.newHashMap();

    private AnnotationProcessorFactory() {
    }

    public AnnotationProcessorFactory create() {
        return new AnnotationProcessorFactory();
    }

    public AnnotationProcessorFactory addBeanPackage(String packageName) {
        scanPackage.add(packageName);
        return this;
    }

    public AnnotationProcessorFactory registryBean(Class<? extends AbstractAutoProcessModel> clazz) {
        //所有抽取规则
        allExtractors.put(clazz, new ModelExtractor(clazz, this));
        //存在match seed的bean,可以标记为种子处理器,会被注册为processor
        AnnotationSeedProcessor.MatchStrategy matchStrategy = judgeMatchStrategy(clazz);
        if (matchStrategy != null) {
            annotationSeedProcessors.add(new AnnotationSeedProcessor<>(clazz, this, matchStrategy));
        }
        return this;
    }

    private void scan() {
        ClassScanner.SubClassVisitor<AbstractAutoProcessModel> subClassVisitor = new ClassScanner.SubClassVisitor<>(true, AbstractAutoProcessModel.class);
        ClassScanner.scan(subClassVisitor, scanPackage);
        List<Class<? extends AbstractAutoProcessModel>> subClass = subClassVisitor.getSubClass();
        //所有抽取规则
        for (Class<? extends AbstractAutoProcessModel> extractModelClass : subClass) {
            registryBean(extractModelClass);
        }
    }

    private AnnotationSeedProcessor.MatchStrategy judgeMatchStrategy(Class<? extends AbstractAutoProcessModel> aClass) {
        AutoProcessor autoProcessor = aClass.getAnnotation(AutoProcessor.class);
        Preconditions.checkNotNull(autoProcessor);
        String seedPattern = autoProcessor.seedPattern();
        if (StringUtils.isNotBlank(seedPattern)) {
            final Pattern pattern = Pattern.compile(seedPattern);
            return new AnnotationSeedProcessor.MatchStrategy() {
                @Override
                public boolean matchSeed(Seed seed) {
                    return pattern.matcher(seed.getData()).matches();
                }
            };
        }

        Method[] methods = aClass.getMethods();
        for (final Method method : methods) {
            if (method.getAnnotation(MatchSeed.class) == null) {
                continue;
            }
            Preconditions.checkArgument(Boolean.class.isAssignableFrom(method.getReturnType()));
            Preconditions.checkArgument(Modifier.isStatic(method.getModifiers()));
            return new AnnotationSeedProcessor.MatchStrategy() {
                @Override
                public boolean matchSeed(Seed seed) {
                    try {
                        return (Boolean) method.invoke(null, seed);
                    } catch (Exception e) {
                        throw new IllegalStateException("can not jude seed match method", e);
                    }
                }
            };
        }
        return null;
    }

    ModelExtractor findExtractor(Class<? extends AbstractAutoProcessModel> clazz) {
        return allExtractors.get(clazz);
    }

    private SeedProcessor build() {
        scan();
        for (ModelExtractor modelExtractor : allExtractors.values()) {
            modelExtractor.init();
        }
        RouteProcessor routeProcessor = new RouteProcessor();
        for (AnnotationSeedProcessor annotationSeedProcessor : annotationSeedProcessors) {
            routeProcessor.addRouter(annotationSeedProcessor);
        }
        return routeProcessor;
    }
}
