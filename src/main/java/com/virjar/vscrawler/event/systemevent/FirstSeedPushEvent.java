package com.virjar.vscrawler.event.systemevent;

import com.virjar.vscrawler.event.support.AutoEvent;
import com.virjar.vscrawler.seed.Seed;

/**
 * Created by virjar on 17/5/20.<br/>
 * 用来当种子为空的时候,爬虫可能停止,这个时候如果来了新的种子,那么通过这个消息激活爬虫任务分发器
 */
public interface FirstSeedPushEvent {
    @AutoEvent
    void firstSeed(Seed seed);
}
