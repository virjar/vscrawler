package com.virjar.vscrawler.core.event.systemevent;

import com.virjar.vscrawler.core.event.support.AutoEvent;
import com.virjar.vscrawler.core.net.session.CrawlerSession;

/**
 * Created by virjar on 17/6/3.
 */
public interface SessionDestroyEvent {
    @AutoEvent(sync = true)
    void onSessionDestroy(CrawlerSession crawlerSession);
}
