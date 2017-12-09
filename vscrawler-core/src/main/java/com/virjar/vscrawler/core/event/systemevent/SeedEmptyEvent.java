package com.virjar.vscrawler.core.event.systemevent;

import com.virjar.vscrawler.core.VSCrawlerContext;
import com.virjar.vscrawler.core.event.support.AutoEvent;

/**
 * Created by virjar on 17/5/20.
 */
public interface SeedEmptyEvent {
    @AutoEvent
    void onSeedEmpty(VSCrawlerContext vsCrawlerContext);
}
