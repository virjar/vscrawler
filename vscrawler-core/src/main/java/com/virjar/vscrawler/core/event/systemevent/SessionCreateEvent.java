package com.virjar.vscrawler.core.event.systemevent;

import com.virjar.vscrawler.core.event.support.AutoEvent;
import com.virjar.vscrawler.core.net.session.CrawlerSession;

/**
 * Created by virjar on 17/4/30.
 * 
 * @author virjar
 * @since 0.0.1
 */
public interface SessionCreateEvent {
    @AutoEvent(sync = true)
    void onSessionCreateEvent(CrawlerSession crawlerSession);
}
