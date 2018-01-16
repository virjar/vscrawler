package com.virjar.vscrawler.core.event.systemevent;

import com.virjar.vscrawler.core.VSCrawlerContext;
import com.virjar.vscrawler.core.event.support.AutoEvent;
import com.virjar.vscrawler.core.net.session.CrawlerSession;

/**
 * Created by virjar on 2018/1/16.<br>
 * 当session资源被回收的回调
 *
 * @author virjar
 * @since 0.2.5
 */
public interface SessionRecycleEvent {
    @AutoEvent(sync = true)
    void onSessionRecycle(VSCrawlerContext vsCrawlerContext, CrawlerSession crawlerSession);
}
