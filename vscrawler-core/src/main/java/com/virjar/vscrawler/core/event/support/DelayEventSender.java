package com.virjar.vscrawler.core.event.support;

import com.virjar.vscrawler.core.VSCrawlerContext;

import java.lang.reflect.Proxy;

/**
 * Created by virjar on 17/6/17.
 */
public class DelayEventSender<T> {
    private Class<T> clazz;
    private long activeTime = 0;
    private VSCrawlerContext vsCrawlerContext;

    DelayEventSender(Class<T> clazz, long afterMillis, VSCrawlerContext vsCrawlerContext) {
        this.clazz = clazz;
        this.vsCrawlerContext = vsCrawlerContext;
        if (afterMillis < 0) {
            afterMillis = 0;
        }
        activeTime = System.currentTimeMillis() + afterMillis;
    }

    public DelayEventSender<T> sendDelay(long afterMillis) {
        if (afterMillis < 0) {
            afterMillis = 0;
        }
        activeTime = System.currentTimeMillis() + afterMillis;
        return this;
    }

    @SuppressWarnings("unchecked")
    public T delegate() {
        DelayEventHandler delayEventHandler = new DelayEventHandler(activeTime, vsCrawlerContext);
        return (T) Proxy.newProxyInstance(DelayEventSender.class.getClassLoader(), new Class[]{clazz},
                delayEventHandler);
    }
}
