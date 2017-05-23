package com.virjar.vscrawler.core.event.systemevent;

import com.virjar.vscrawler.core.event.support.AutoEvent;

/**
 * Created by virjar on 17/5/15.<br/>
 * 爬虫停止的时候触发的事件,事件是同步的,因为这个时候可能jvm程序都停止运行,事件循环线程也被终止。<br/>
 * 如果是这个场景,那么线程可能在一个单独的jvm环境中(jvm停止钩子线程)。所以,千万记住,本事件中<br/>
 * 不允许调用耗时逻辑,如网络访问,锁等待
 *
 */

public interface CrawlerEndEvent {
    /**
     * 爬虫终止事件为同步事件,不占用事件循环线程
     */
    @AutoEvent(sync = true)
    void crawlerEnd();
}
