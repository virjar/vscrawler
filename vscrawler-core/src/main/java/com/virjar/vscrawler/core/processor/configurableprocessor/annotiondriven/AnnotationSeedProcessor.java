package com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven;

import com.virjar.vscrawler.core.net.session.CrawlerSession;
import com.virjar.vscrawler.core.processor.BindRouteProcessor;
import com.virjar.vscrawler.core.processor.CrawlResult;
import com.virjar.vscrawler.core.seed.Seed;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by virjar on 2017/12/10.<br/>
 * 基于注解的处理器
 * *
 *
 * @author virjar
 * @since 0.2.1
 */
@Slf4j
public class AnnotationSeedProcessor<T extends AbstractAutoProcessModel> implements BindRouteProcessor {
    private MatchStrategy matchStrategy;
    private ModelExtractor<? extends AbstractAutoProcessModel> modelExtractor;

    public AnnotationSeedProcessor(Class<T> aClass, AnnotationProcessorFactory annotationProcessorFactory, MatchStrategy matchStrategy) {
        this.matchStrategy = matchStrategy;
        this.modelExtractor = annotationProcessorFactory.findExtractor(aClass);
    }


    @Override
    public void process(Seed seed, CrawlerSession crawlerSession, CrawlResult crawlResult) {
        modelExtractor.process(seed, crawlerSession, crawlResult);
    }

    @Override
    public boolean matchSeed(Seed seed) {
        return matchStrategy.matchSeed(seed);
    }

    public interface MatchStrategy {
        boolean matchSeed(Seed seed);
    }


}
