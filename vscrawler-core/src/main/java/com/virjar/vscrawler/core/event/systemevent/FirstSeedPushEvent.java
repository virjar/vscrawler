package com.virjar.vscrawler.core.event.systemevent;

import com.virjar.vscrawler.core.VSCrawlerContext;
import com.virjar.vscrawler.core.event.support.AutoEvent;
import com.virjar.vscrawler.core.seed.Seed;

/**
 * Created by virjar on 17/5/20.<br/>
 * 用来当种子为空的时候,爬虫可能停止,这个时候如果来了新的种子,那么通过这个消息激活爬虫任务分发器
 */
public interface FirstSeedPushEvent {
    @AutoEvent
    void firstSeed(VSCrawlerContext vsCrawlerContext, Seed seed);
}
