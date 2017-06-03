package com.virjar.vscrawler.core.net.session;

import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.aviator.AviatorEvaluator;
import com.virjar.vscrawler.core.event.support.AutoEventRegistry;
import com.virjar.vscrawler.core.event.systemevent.CrawlerConfigChangeEvent;
import com.virjar.vscrawler.core.event.systemevent.CrawlerEndEvent;
import com.virjar.vscrawler.core.event.systemevent.SessionCreateEvent;
import com.virjar.vscrawler.core.net.CrawlerHttpClientGenerator;
import com.virjar.vscrawler.core.net.proxy.IPPool;
import com.virjar.vscrawler.core.net.proxy.strategy.ProxyPlanner;
import com.virjar.vscrawler.core.net.proxy.strategy.ProxyStrategy;
import com.virjar.vscrawler.core.util.SingtonObjectHolder;
import com.virjar.vscrawler.core.util.VSCrawlerConstant;

/**
 * Created by virjar on 17/4/15.<br/>
 * 创建并管理多个用户的链接,pool逻辑大概是模仿druid实现的
 * 
 * @author virjar
 * @since 0.0.1
 */
public class CrawlerSessionPool implements CrawlerConfigChangeEvent, CrawlerEndEvent {

    private static final Logger logger = LoggerFactory.getLogger(CrawlerSessionPool.class);

    private LinkedBlockingQueue<CrawlerSession> allSessions = new LinkedBlockingQueue<>();

    private long maxIdle = 10;

    private long minIdle = 0;

    private long reuseDuration = 60 * 60 * 1000;

    private int maxOccurs = 1;

    private int maxActive = 100;

    private int initialSize = 0;

    // 分发session最多等待时间
    private long maxWait = -1L;

    private CrawlerHttpClientGenerator crawlerHttpClientGenerator;

    /**
     * 代理切换策略
     */
    private ProxyStrategy proxyStrategy;

    private IPPool ipPool;
    private ProxyPlanner proxyPlanner = null;

    // 是否初始化
    protected volatile boolean inited = false;

    private ReentrantLock lock = new ReentrantLock();
    protected Condition notEmpty = lock.newCondition();
    protected Condition empty = lock.newCondition();

    private CreateSessionThread createSessionThread;

    public CrawlerSessionPool(CrawlerHttpClientGenerator crawlerHttpClientGenerator, ProxyStrategy proxyStrategy,
            IPPool ipPool, ProxyPlanner proxyPlanner) {
        this.crawlerHttpClientGenerator = crawlerHttpClientGenerator;
        this.proxyStrategy = proxyStrategy;
        this.ipPool = ipPool;
        this.proxyPlanner = proxyPlanner;

        // 注册事件监听,接收配置文件变更消息,下面两句比较巧妙
        changeWithProperties(SingtonObjectHolder.vsCrawlerConfigFileWatcher.loadedProperties());
        AutoEventRegistry.getInstance().registerObserver(this);

    }

    public void init() {
        if (inited) {
            return;
        }
        try {
            lock.lockInterruptibly();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {

            if (inited) {
                return;
            }

            if (maxActive <= 0) {
                throw new IllegalArgumentException("illegal maxActive " + maxActive);
            }

            if (maxActive < minIdle) {
                throw new IllegalArgumentException("illegal maxActive " + maxActive);
            }

            if (initialSize > maxActive) {
                throw new IllegalArgumentException(
                        "illegal initialSize " + this.initialSize + ", maxActive " + maxActive);
            }

            createSessionThread = new CreateSessionThread();
            createSessionThread.start();
        } finally {
            inited = true;
            lock.unlock();
        }
    }

    private CrawlerSession createNewSession() {
        CrawlerSession crawlerSession = new CrawlerSession(crawlerHttpClientGenerator, proxyStrategy, ipPool,
                proxyPlanner);
        AutoEventRegistry.getInstance().findEventDeclaring(SessionCreateEvent.class)
                .onSessionCreateEvent(crawlerSession);
        if (crawlerSession.isValid()) {
            return crawlerSession;
        }
        return null;
    }

    public CrawlerSession borrowOne(long maxWaitMillis) {
        init();
        long lessTimeMillis = maxWaitMillis;

        // logger.info("当前会话池中,共有:{}个用户可用", allSessions.size());
        for (;;) {
            long startRequestTimeStamp = System.currentTimeMillis();
            CrawlerSession crawlerSession = getSessionInternal(lessTimeMillis);
            if (crawlerSession == null && lessTimeMillis < 0) {
                return null;
            }
            if (crawlerSession == null) {// 如果系统本身线程数不够,则使用主调线程,此方案后续讨论是否合适
                crawlerSession = createNewSession();
            }
            lessTimeMillis = lessTimeMillis - (System.currentTimeMillis() - startRequestTimeStamp);
            if (crawlerSession == null) {
                continue;
            }

            // 各种check

            return crawlerSession;
        }
    }

    private CrawlerSession getSessionInternal(long maxWait) {
        try {
            lock.lockInterruptibly();
        } catch (InterruptedException e) {
            throw new PoolException("lock interrupted", e);
        }

        try {
            if (allSessions.size() == 0) {
                empty.signal();
            }
            if (maxWait > 0) {
                return allSessions.poll(maxWait, TimeUnit.MILLISECONDS);
            } else {
                return allSessions.poll();
            }
        } catch (InterruptedException interruptedException) {
            throw new PoolException("lock interrupted", interruptedException);
        } finally {
            lock.unlock();
        }
    }

    private void changeWithProperties(Properties properties) {
        maxActive = NumberUtils.toInt(
                AviatorEvaluator.exec(properties.getProperty(VSCrawlerConstant.SESSION_POOL_ACTIVE_USER)).toString(),
                Integer.MAX_VALUE);
        maxIdle = NumberUtils.toInt(
                AviatorEvaluator.exec(properties.getProperty(VSCrawlerConstant.SESSION_POOL_MAX_IDLE)).toString(),
                150000);

        reuseDuration = NumberUtils.toInt(
                AviatorEvaluator.exec(properties.getProperty(VSCrawlerConstant.SESSION_POOL_MAX_DURATION)).toString(),
                3600000);
        maxOccurs = NumberUtils.toInt(
                AviatorEvaluator.exec(properties.getProperty(VSCrawlerConstant.SESSION_POOL_MAX_OCCURS)).toString(), 1);
        minIdle = NumberUtils.toInt(
                AviatorEvaluator.exec(properties.getProperty(VSCrawlerConstant.SESSION_POOL_MIN_IDLE)).toString(),
                10000);
        int newThreadNumber = NumberUtils.toInt(AviatorEvaluator
                .exec(properties.getProperty(VSCrawlerConstant.SESSION_POOL_MONTOR_THREAD_NUMBER)).toString(), 2);
    }

    /**
     * 当配置信息变更过来的时候,会回调这里
     * 
     * @param oldProperties 旧配置文件内容
     * @param newProperties 新配置文件内容
     */
    @Override
    public void configChange(Properties oldProperties, Properties newProperties) {
        changeWithProperties(newProperties);
    }

    @Override
    public void crawlerEnd() {
        createSessionThread.interrupt();
    }

    private class CreateSessionThread extends Thread {
        CreateSessionThread() {
            super("createNewSession");
            setDaemon(true);
        }

        @Override
        public void run() {
            super.run();
        }
    }
}
