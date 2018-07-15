package com.virjar.vscrawler.core.seed;

import com.virjar.vscrawler.core.VSCrawlerContext;
import com.virjar.vscrawler.core.event.support.AutoEvent;

/**
 * Created by virjar on 2018/7/15.<br>
 * 种子资源导入,可能导入巨量种子,这个可能导致内存溢出,也会由于初始化任务卡死爬虫启动流程。所以通过这个插件构造异步导入事件<br>
 * 通过事件驱动的方式,在爬虫启动之后,分批次导入种子任务
 */
public interface LoadNextBatchSeedEvent {
    @AutoEvent
    void nextBatch(VSCrawlerContext vsCrawlerContext);
}
