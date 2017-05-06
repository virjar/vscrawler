package com.virjar.vscrawler.event.systemevent;

import com.virjar.vscrawler.event.support.AutoEvent;

/**
 * Created by virjar on 17/5/6.
 */
public interface CrawlerStartEvent {
    @AutoEvent
    void onCrawlerStart();
}
