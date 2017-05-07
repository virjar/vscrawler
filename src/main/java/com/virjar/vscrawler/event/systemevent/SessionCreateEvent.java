package com.virjar.vscrawler.event.systemevent;

import com.virjar.vscrawler.event.support.AutoEvent;
import com.virjar.vscrawler.net.session.CrawlerSession;
import com.virjar.vscrawler.net.user.User;

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
