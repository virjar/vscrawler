package com.virjar.vscrawler.core.processor;

import java.util.Collection;
import java.util.LinkedList;

import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.net.session.CrawlerSession;
import com.virjar.vscrawler.core.seed.Seed;

/**
 * Created by virjar on 17/6/17.
 */
public class RouteProcessor implements SeedProcessor {

    private LinkedList<SeedRouter> seedRouters = Lists.newLinkedList();

    public void addRouter(SeedRouter seedRouter) {
        seedRouters.add(seedRouter);
    }

    public void addRouters(Collection<SeedRouter> seedRouters) {
        this.seedRouters.addAll(seedRouters);
    }

    @Override
    public void process(Seed seed, CrawlerSession crawlerSession, CrawlResult crawlResult) {
        for (SeedRouter seedRouter : seedRouters) {
            if (seedRouter.matchSeed(seed)) {
                seedRouter.process(seed, crawlerSession, crawlResult);
            }
        }
    }
}
