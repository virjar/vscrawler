package com.virjar.vscrawler.core.processor;

import com.google.common.collect.Maps;
import com.virjar.vscrawler.core.net.session.CrawlerSession;
import com.virjar.vscrawler.core.seed.Seed;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by virjar on 17/6/17.
 */
@Slf4j
public class RouteProcessor implements SeedProcessor {

    private ConcurrentMap<SeedRouter, SeedProcessor> allRouters = Maps.newConcurrentMap();

    public void addRouter(SeedRouter seedRouter, SeedProcessor seedProcessor) {
        allRouters.put(seedRouter, seedProcessor);
    }

    public void addRouter(BindRouteProcessor bindRouteProcessor) {
        addRouter(bindRouteProcessor, bindRouteProcessor);
    }

    public void addRouters(Collection<BindRouteProcessor> seedRouters) {
        for (BindRouteProcessor bindRouteProcessor : seedRouters) {
            addRouter(bindRouteProcessor);
        }
    }

    @Override
    public void process(Seed seed, CrawlerSession crawlerSession, CrawlResult crawlResult) {
        boolean hasProcessed = false;
        for (SeedRouter seedRouter : allRouters.keySet()) {
            if (seedRouter.matchSeed(seed)) {
                hasProcessed = true;
                allRouters.get(seedRouter).process(seed, crawlerSession, crawlResult);
            }
        }
        if (!hasProcessed) {
            log.warn("can not find processor for seed:{} ", seed.getData());
        }
    }
}
