package com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven;

import com.google.common.base.Preconditions;
import com.virjar.sipsoup.util.ObjectFactory;
import com.virjar.vscrawler.core.net.session.CrawlerSession;
import com.virjar.vscrawler.core.processor.BindRouteProcessor;
import com.virjar.vscrawler.core.processor.CrawlResult;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.annotation.DownLoadMethod;
import com.virjar.vscrawler.core.seed.Seed;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * Created by virjar on 2017/12/10.<br/>
 * 基于注解的处理器
 * *
 *
 * @author virjar
 * @since 0.2.1
 */
@Slf4j
class AnnotationSeedProcessor implements BindRouteProcessor {
    private MatchStrategy matchStrategy;
    private ModelExtractor modelExtractor;
    private Downloader downloader;
    private Class<? extends AbstractAutoProcessModel> aClass;

    AnnotationSeedProcessor(Class<? extends AbstractAutoProcessModel> aClass, AnnotationProcessorBuilder annotationProcessorBuilder, MatchStrategy matchStrategy) {
        this.matchStrategy = matchStrategy;
        this.modelExtractor = annotationProcessorBuilder.findExtractor(aClass);
        this.aClass = aClass;
        judgeDownloader(aClass);
    }


    @Override
    public void process(Seed seed, CrawlerSession crawlerSession, CrawlResult crawlResult) {
        //创建模型对象
        AbstractAutoProcessModel model = ObjectFactory.newInstance(aClass);
        String content = downloader.download(seed, model, crawlerSession);
        modelExtractor.process(seed, content, crawlResult, model, null, true);
    }

    @Override
    public boolean matchSeed(Seed seed) {
        return matchStrategy.matchSeed(seed);
    }

    interface MatchStrategy {
        boolean matchSeed(Seed seed);
    }

    private interface Downloader {
        String download(Seed seed, AbstractAutoProcessModel model, CrawlerSession crawlerSession);
    }

    private void judgeDownloader(Class<? extends AbstractAutoProcessModel> aClass) {
        Method[] methods = aClass.getMethods();
        for (final Method method : methods) {
            if (method.getAnnotation(DownLoadMethod.class) == null) {
                continue;
            }
            Preconditions.checkArgument(String.class.isAssignableFrom(method.getReturnType()));
            Preconditions.checkArgument(method.getParameterTypes().length >= 2);
            Preconditions.checkArgument(method.getParameterTypes()[0].isAssignableFrom(Seed.class));
            Preconditions.checkArgument(method.getParameterTypes()[1].isAssignableFrom(CrawlerSession.class));

            downloader = new Downloader() {
                @Override
                public String download(Seed seed, AbstractAutoProcessModel model, CrawlerSession crawlerSession) {
                    try {
                        return (String) method.invoke(model, seed, crawlerSession);
                    } catch (Exception e) {
                        throw new RuntimeException("invoke download method :" + method.toGenericString() + " failed", e);
                    }
                }
            };
            return;
        }

        downloader = new Downloader() {
            @Override
            public String download(Seed seed, AbstractAutoProcessModel model, CrawlerSession crawlerSession) {
                return crawlerSession.getCrawlerHttpClient().get(seed.getData());
            }
        };
    }

}
