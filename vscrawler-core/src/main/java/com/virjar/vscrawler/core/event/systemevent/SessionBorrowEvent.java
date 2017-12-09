package com.virjar.vscrawler.core.event.systemevent;

import com.virjar.vscrawler.core.VSCrawlerContext;
import com.virjar.vscrawler.core.event.support.AutoEvent;
import com.virjar.vscrawler.core.net.session.CrawlerSession;

/**
 * Created by virjar on 17/6/4.
 */
public interface SessionBorrowEvent {
    @AutoEvent(sync = true)
    void onSessionBorrow(VSCrawlerContext vsCrawlerContext, CrawlerSession crawlerSession);
}
