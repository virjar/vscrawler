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
import java.util.regex.PatternSyntaxException;

/**
 * Created by virjar on 2017/12/12.<br/>
 * 加载所有的的基于注解的爬虫
 *
 * @author virjar
 * @since 0.2.1
 */
public class AnnotationProcessorBuilder {
    private List<String> scanPackage = Lists.newLinkedList();
    private List<AnnotationSeedProcessor> annotationSeedProcessors = Lists.newLinkedList();
    private Map<Class<? extends AbstractAutoProcessModel>, ModelExtractor> allExtractors = Maps.newHashMap();

    private AnnotationProcessorBuilder() {
    }

    public static AnnotationProcessorBuilder create() {
        return new AnnotationProcessorBuilder();
    }

    public AnnotationProcessorBuilder addBeanPackage(String packageName) {
        scanPackage.add(packageName);
        return this;
    }

    public AnnotationProcessorBuilder registryBean(Class<? extends AbstractAutoProcessModel> clazz) {
        if (allExtractors.containsKey(clazz)) {
            return this;
        }
        //所有抽取规则
        allExtractors.put(clazz, new ModelExtractor(clazz, this));
        //存在match seed的bean,可以标记为种子处理器,会被注册为processor
        AnnotationSeedProcessor.MatchStrategy matchStrategy = judgeMatchStrategy(clazz);
        if (matchStrategy != null) {
            annotationSeedProcessors.add(new AnnotationSeedProcessor(clazz, this, matchStrategy));
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
        final AutoProcessor autoProcessor = aClass.getAnnotation(AutoProcessor.class);
        if (autoProcessor == null) {
            return null;
        }
        String seedPattern = autoProcessor.seedPattern();
        if (StringUtils.isNotBlank(seedPattern)) {
            try {
                final Pattern pattern = Pattern.compile(seedPattern);
                return new AnnotationSeedProcessor.MatchStrategy() {
                    @Override
                    public boolean matchSeed(Seed seed) {
                        return pattern.matcher(seed.getData()).matches();
                    }

                    @Override
                    public int priority() {
                        return autoProcessor.priority();
                    }
                };
            } catch (PatternSyntaxException e) {
                throw new IllegalStateException("error when register processor for class" + aClass.getName() + " regex error for seedPattern", e);
            }

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

                @Override
                public int priority() {
                    return autoProcessor.priority();
                }
            };
        }
        return null;
    }

    ModelExtractor findExtractor(Class<? extends AbstractAutoProcessModel> clazz) {
        return allExtractors.get(clazz);
    }

    public SeedProcessor build() {
        if (allExtractors.size() == 0 || scanPackage.size() > 0) {
            scan();
        }
        if (allExtractors.size() == 0) {
            throw new IllegalStateException("can not find any auto processor model,please check you configuration");
        }
        for (ModelExtractor modelExtractor : allExtractors.values()) {
            modelExtractor.init();
        }
        RouteProcessor routeProcessor = new RouteProcessor();
        for (AnnotationSeedProcessor annotationSeedProcessor : annotationSeedProcessors) {
            routeProcessor.addRouter(annotationSeedProcessor, annotationSeedProcessor.priority());
        }
        return routeProcessor;
    }
}
