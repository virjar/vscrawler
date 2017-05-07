package com.virjar.vscrawler.net.session;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.client.CookieStore;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.BasicCookieStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.virjar.dungproxy.client.httpclient.CrawlerHttpClient;
import com.virjar.dungproxy.client.httpclient.conn.ProxyBindRoutPlanner;
import com.virjar.vscrawler.event.support.AutoEventRegistry;
import com.virjar.vscrawler.event.systemevent.SessionCreateEvent;
import com.virjar.vscrawler.net.CrawlerHttpClientGenerator;
import com.virjar.vscrawler.net.proxy.ProxyStrategy;
import com.virjar.vscrawler.net.proxy.VSCrawlerRoutePlanner;
import com.virjar.vscrawler.net.user.User;

import lombok.Getter;
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
    /**
     * session 持有的用户
     */
    @Getter
    private User user;

    private AtomicBoolean enable = new AtomicBoolean(false);

    @Getter
    private CookieStore cookieStore = new BasicCookieStore();

    private AtomicInteger borrowNumber = new AtomicInteger(0);

    private LoginHandler loginHandler;

    @Getter
    private CrawlerHttpClient crawlerHttpClient;

    @Getter
    private long lastActiveTimeStamp = 0L;

    @Getter
    private long initTimeStamp = 0L;

    private AtomicBoolean isLoginning = new AtomicBoolean(false);

    private ProxyStrategy proxyStrategy;

    @Getter
    private Map<String, Object> ext = Maps.newHashMap();

    public CrawlerSession(User user, LoginHandler loginHandler, CrawlerHttpClientGenerator crawlerHttpClientGenerator,
            ProxyStrategy proxyStrategy) {
        this.loginHandler = loginHandler;
        this.user = user;
        user.holdUser(this);
        this.crawlerHttpClient = crawlerHttpClientGenerator.gen(cookieStore);
        this.proxyStrategy = proxyStrategy;
        // 对代理IP策略进行路由
        crawlerHttpClient.setRoutePlanner(decorateRoutePlanner(crawlerHttpClient.getRoutePlanner()));

        AutoEventRegistry.getInstance().findEventDeclaring(SessionCreateEvent.class).onSessionCreateEvent(this);
        // new SessionCreateEvent(user).send();
        login();
    }

    private HttpRoutePlanner decorateRoutePlanner(HttpRoutePlanner routePlanner) {
        if (proxyStrategy == ProxyStrategy.NONE) {// 如果是不代理,那么不修改
            return routePlanner;
        }

        if (!(routePlanner instanceof ProxyBindRoutPlanner)) {
            log.warn("自定义了代理发生器,vscrawler的代理功能将不会生效");
            return routePlanner;
        }
        return new VSCrawlerRoutePlanner((ProxyBindRoutPlanner) routePlanner, proxyStrategy, this);
    }

    public boolean login() {
        if (isLoginning.compareAndSet(false, true)) {
            try {
                if (!user.checkHold(this)) {// TODO 这个逻辑确认一下
                    logger.warn("当前session持有的用户被其他session占用,本session停止工作");
                    return false;
                }

                boolean ret = loginHandler == null || loginHandler.onLogin(user, cookieStore, crawlerHttpClient);
                logger.info("用户:{} 登录:{}", user.getUserID(), ret ? "成功" : "失败");
                if (ret) {
                    initTimeStamp = lastActiveTimeStamp = System.currentTimeMillis();
                    enable.set(true);
                }
                return ret;
            } finally {
                isLoginning.set(false);
            }
        }
        return false;
    }

    public boolean isLogin() {
        return isLoginning.get();
    }

    public void recordBorrow() {
        lastActiveTimeStamp = System.currentTimeMillis();
        borrowNumber.incrementAndGet();
    }

    public int borrowTimes() {
        return borrowNumber.get();
    }

    public boolean testLoginState() {
        if (loginHandler == null) {
            return true;
        }
        if (!enable.get()) {
            synchronized (this) {
                return enable.get() || login();
            }
        }
        boolean loginSuccess = loginHandler.testLogin(cookieStore, crawlerHttpClient);
        if (loginSuccess) {
            lastActiveTimeStamp = System.currentTimeMillis();
        }
        return loginSuccess;
    }

    /**
     * 归还session
     *
     * @param isSessionEnabled 当前session是否失效,需要由业务层判断
     */
    public void feedback(boolean isSessionEnabled) {
        if (!isSessionEnabled && !login()) {
            enable.set(false);
        }
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
