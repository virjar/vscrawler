package com.virjar.vscrawler.core.event.systemevent;

import com.virjar.vscrawler.core.VSCrawlerContext;
import com.virjar.vscrawler.core.event.support.AutoEvent;
import com.virjar.vscrawler.core.seed.Seed;

import java.util.Collection;

/**
 * Created by virjar on 17/5/16.
 */
public interface NewSeedArrivalEvent {
    @AutoEvent
    void needSeed(VSCrawlerContext vsCrawlerContext, Collection<Seed> newSeeds);
}
