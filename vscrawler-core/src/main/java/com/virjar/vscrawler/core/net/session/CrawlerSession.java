package com.virjar.vscrawler.core.net.session;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.client.CookieStore;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.virjar.dungproxy.client.httpclient.CrawlerHttpClient;
import com.virjar.dungproxy.client.httpclient.conn.ProxyBindRoutPlanner;
import com.virjar.vscrawler.core.event.support.AutoEventRegistry;
import com.virjar.vscrawler.core.event.systemevent.SessionCreateEvent;
import com.virjar.vscrawler.core.net.CrawlerHttpClientGenerator;
import com.virjar.vscrawler.core.net.proxy.IPPool;
import com.virjar.vscrawler.core.net.proxy.VSCrawlerRoutePlanner;
import com.virjar.vscrawler.core.net.proxy.strategy.*;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by virjar on 17/4/15. <br/>
 * 一个会话,持有到目标网站的cookie数据
 *
 * @author virjar
 * @since 0.0.1
 */
@Slf4j
public class CrawlerSession {
    private static final Logger logger = LoggerFactory.getLogger(CrawlerSession.class);

    private AtomicBoolean enable = new AtomicBoolean(false);

    @Getter
    private CookieStore cookieStore;

    private AtomicInteger borrowNumber = new AtomicInteger(0);

    @Getter
    private CrawlerHttpClient crawlerHttpClient;

    @Getter
    private long lastActiveTimeStamp = 0L;

    @Getter
    private long initTimeStamp = 0L;

    @Getter
    private long userCount = 0L;

    private ProxyStrategy proxyStrategy;

    private ProxyPlanner proxyPlanner;

    private IPPool ipPool;

    @Getter
    @Setter
    private boolean valid = true;

    private Map<String, Object> ext = Maps.newHashMap();


    public CrawlerSession(CrawlerHttpClientGenerator crawlerHttpClientGenerator, ProxyStrategy proxyStrategy,
            IPPool ipPool, ProxyPlanner proxyPlanner) {
        ProxyFeedBackDecorateHttpClientBuilder proxyFeedBackDecorateHttpClientBuilder = new ProxyFeedBackDecorateHttpClientBuilder();
        this.crawlerHttpClient = crawlerHttpClientGenerator.gen(proxyFeedBackDecorateHttpClientBuilder);
        Preconditions.checkArgument(proxyFeedBackDecorateHttpClientBuilder.isBuild(),
                "必须使用指定的HttpclientBuilder构造httpclient");
        this.proxyStrategy = proxyStrategy;
        this.ipPool = ipPool;
        this.proxyPlanner = proxyPlanner;

        determineProxyPlanner();
        // 对代理IP策略进行路由
        decorateRoutePlanner(crawlerHttpClient);

        this.cookieStore = crawlerHttpClient.getCookieStore();
        AutoEventRegistry.getInstance().findEventDeclaring(SessionCreateEvent.class).onSessionCreateEvent(this);
    }


    public Object getExtInfo(String key){
        return ext.get(key);
    }

    public void setExtInfo(String key,Object obj){
        ext.put(key,obj);
    }

    private void determineProxyPlanner() {
        switch (proxyStrategy) {
        case CUSTOM:
            if (proxyPlanner == null) {
                throw new IllegalStateException("您选择了自定义代理决策方案,但是没有设置代理决策器");
            }
            break;
        case REQUEST:
            proxyPlanner = new EveryRequestPlanner();
            break;
        case SESSION:
            proxyPlanner = new EverySessionPlanner();
            break;
        case USER:
            proxyPlanner = new EveryUserPlanner();
            break;
        case NONE:
            proxyPlanner = new NotProxyPlanner();
            break;
        default:
            proxyPlanner = new NotProxyPlanner();
        }

    }

    private void decorateRoutePlanner(CrawlerHttpClient crawlerHttpClient) {
        HttpRoutePlanner routePlanner = crawlerHttpClient.getRoutePlanner();
        if (!(routePlanner instanceof ProxyBindRoutPlanner)) {
            log.warn("自定义了代理发生器,vscrawler的代理功能将不会生效");
            return;
        }

        VSCrawlerRoutePlanner vsCrawlerRoutePlanner = new VSCrawlerRoutePlanner((ProxyBindRoutPlanner) routePlanner,
                ipPool, proxyPlanner, this);
        crawlerHttpClient.setRoutePlanner(vsCrawlerRoutePlanner);

    }

    public void recordBorrow() {
        lastActiveTimeStamp = System.currentTimeMillis();
        borrowNumber.incrementAndGet();
    }

    public int borrowTimes() {
        return borrowNumber.get();
    }

    /**
     * 归还session
     *
     */
    public void feedback() {
        borrowNumber.decrementAndGet();
    }

    /**
     * 清空session
     */
    public void destory() {
        cookieStore.clear();
        enable.set(false);
    }

    public boolean getEnable() {
        return enable.get();
    }
}
