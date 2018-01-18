package com.virjar.vscrawler.core.processor;

import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.net.session.CrawlerSession;
import com.virjar.vscrawler.core.seed.Seed;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by virjar on 17/6/17.
 */
@Slf4j
public class RouteProcessor implements SeedProcessor {

    private List<PriopityProcessorHolder> allProcessor = Lists.newArrayList();
    private boolean hasSorted = false;

    public void addRouter(BindRouteProcessor bindRouteProcessor) {
        allProcessor.add(new PriopityProcessorHolder(bindRouteProcessor, 0));
    }

    public void addRouter(BindRouteProcessor bindRouteProcessor, int priority) {
        allProcessor.add(new PriopityProcessorHolder(bindRouteProcessor, priority));
    }

    public void addRouters(Collection<BindRouteProcessor> seedRouters) {
        for (BindRouteProcessor bindRouteProcessor : seedRouters) {
            addRouter(bindRouteProcessor);
        }
    }

    @Override
    public void process(Seed seed, CrawlerSession crawlerSession, CrawlResult crawlResult) {
        if (!hasSorted) {
            synchronized (this) {
                if (!hasSorted) {
                    Collections.sort(allProcessor);
                    hasSorted = true;
                }
            }
        }
        boolean hasProcessed = false;
        for (PriopityProcessorHolder seedRouter : allProcessor) {
            if (seedRouter.matchSeed(seed)) {
                hasProcessed = true;
                seedRouter.process(seed, crawlerSession, crawlResult);
                break;
            }
        }
        if (!hasProcessed) {
            log.warn("can not find processor for seed:{} ", seed.getData());
        }
    }

    @RequiredArgsConstructor
    private static class PriopityProcessorHolder implements BindRouteProcessor, Comparable<PriopityProcessorHolder> {
        @NonNull
        private BindRouteProcessor delegate;
        @NonNull
        @Getter
        private int priority;

        @Override
        public void process(Seed seed, CrawlerSession crawlerSession, CrawlResult crawlResult) {
            delegate.process(seed, crawlerSession, crawlResult);
        }

        @Override
        public boolean matchSeed(Seed seed) {
            return delegate.matchSeed(seed);
        }

        @Override
        public int compareTo(PriopityProcessorHolder o) {
            //逆序
            return Integer.valueOf(o.priority).compareTo(priority);
        }
    }
}
