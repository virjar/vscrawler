package com.virjar.vscrawler.core.net.session;

import com.google.common.collect.Sets;
import com.virjar.dungproxy.client.util.CommonUtil;
import com.virjar.vscrawler.core.VSCrawlerContext;
import com.virjar.vscrawler.core.event.systemevent.CrawlerEndEvent;
import com.virjar.vscrawler.core.event.systemevent.SessionBorrowEvent;
import com.virjar.vscrawler.core.event.systemevent.SessionCreateEvent;
import com.virjar.vscrawler.core.event.systemevent.SessionRecycleEvent;
import com.virjar.vscrawler.core.monitor.VSCrawlerMonitor;
import com.virjar.vscrawler.core.net.CrawlerHttpClientGenerator;
import com.virjar.vscrawler.core.net.proxy.IPPool;
import com.virjar.vscrawler.core.net.proxy.strategy.ProxyPlanner;
import com.virjar.vscrawler.core.net.proxy.strategy.ProxyStrategy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by virjar on 17/4/15.<br/>
 * 创建并管理多个用户的链接,pool逻辑大概是模仿druid实现的
 *
 * @author virjar
 * @since 0.0.1
 */
@Slf4j
public class CrawlerSessionPool implements CrawlerEndEvent {

    private int maxSize = 10;

    private int coreSize = 0;

    private int initialSize = 0;

    private long reuseDuration = 60 * 60 * 1000;

    private long maxOnlineDuration = Long.MAX_VALUE;

    private CrawlerHttpClientGenerator crawlerHttpClientGenerator;

    /**
     * 代理切换策略
     */
    private ProxyStrategy proxyStrategy;

    private IPPool ipPool;
    private ProxyPlanner proxyPlanner = null;

    // 是否初始化
    private volatile boolean inited = false;

    private ReentrantLock lock = new ReentrantLock();
    protected Condition empty = lock.newCondition();

    private SessionDaemonThread sessionDaemonThread;
    private DelayQueue<SessionHolder> idleSessionQueue = new DelayQueue<>();
    private Set<CrawlerSession> leaveSessions = Sets.newConcurrentHashSet();

    @Getter
    private VSCrawlerContext vsCrawlerContext;
    private volatile boolean createNewSessionStatus = true;

    private boolean autoCreateSession = true;

    public CrawlerSessionPool(VSCrawlerContext vsCrawlerContext, CrawlerHttpClientGenerator crawlerHttpClientGenerator, ProxyStrategy proxyStrategy,
                              IPPool ipPool, ProxyPlanner proxyPlanner, int maxSize, int coreSize, int initialSize, long reuseDuration,
                              long maxOnlineDuration, boolean autoCreateSession) {
        this.vsCrawlerContext = vsCrawlerContext;
        this.crawlerHttpClientGenerator = crawlerHttpClientGenerator;
        this.proxyStrategy = proxyStrategy;
        this.ipPool = ipPool;
        this.proxyPlanner = proxyPlanner;
        this.maxSize = maxSize;
        this.coreSize = coreSize;
        this.initialSize = initialSize;
        this.reuseDuration = reuseDuration;
        this.maxOnlineDuration = maxOnlineDuration;
        this.autoCreateSession = autoCreateSession;
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

            if (maxSize < coreSize) {
                throw new IllegalArgumentException("maxSize " + maxSize + "  must grater than coreSize " + coreSize);
            }
            if (initialSize > maxSize) {
                throw new IllegalArgumentException(
                        "maxSize " + maxSize + "  must grater than initialSize " + initialSize);
            }

            if (reuseDuration < 0) {
                reuseDuration = 0;
            }

            if (initialSize > 0) {
                int totalTry = 0;
                for (int i = 0; i < initialSize; i++) {
                    totalTry++;
                    CrawlerSession newSession = createNewSession();
                    if (newSession == null) {
                        i--;
                    } else {
                        idleSessionQueue.offer(new SessionHolder(newSession));
                    }
                    if (totalTry > initialSize * 3) {
                        throw new IllegalStateException("can not create session ,all session create failed");
                    }
                }
            }

            if (autoCreateSession) {
                sessionDaemonThread = new SessionDaemonThread();
                sessionDaemonThread.start();
            }

            vsCrawlerContext.getAutoEventRegistry().registerObserver(this);
        } finally {
            inited = true;
            lock.unlock();
        }
    }

    private CrawlerSession createNewSession() {
        CrawlerSession crawlerSession = new CrawlerSession(crawlerHttpClientGenerator, proxyStrategy, ipPool,
                proxyPlanner, this);
        try {
            vsCrawlerContext.getAutoEventRegistry().findEventDeclaring(SessionCreateEvent.class)
                    .onSessionCreateEvent(vsCrawlerContext, crawlerSession);
        } catch (Exception e) {
            log.error("error when create session", e);
            crawlerSession.setValid(false);
        }
        if (crawlerSession.isValid()) {
            VSCrawlerMonitor.recordOne(vsCrawlerContext.getCrawlerName() + "_create_session_success");
            crawlerSession.setInitTimeStamp(System.currentTimeMillis());
            createNewSessionStatus = true;
            return crawlerSession;
        } else {
            VSCrawlerMonitor.recordOne(vsCrawlerContext.getCrawlerName() + "_create_session_failed");
            createNewSessionStatus = false;
            try {
                crawlerSession.destroy();
            } catch (RuntimeException e) {
                log.error("failed to destroy a session resource", e);
            }
            return null;
        }
    }

    public void recycle(CrawlerSession crawlerSession) {
        leaveSessions.remove(crawlerSession);
        crawlerSession.setLastActiveTimeStamp(System.currentTimeMillis());
        if (idleSessionQueue.size() > maxSize || !crawlerSession.isValid()) {
            crawlerSession.destroy();
        } else {
            idleSessionQueue.offer(new SessionHolder(crawlerSession));
            vsCrawlerContext.getAutoEventRegistry()
                    .findEventDeclaring(SessionRecycleEvent.class)
                    .onSessionRecycle(vsCrawlerContext, crawlerSession);
        }
    }

    public CrawlerSession borrowOne(long maxWaitMillis, boolean forceCreate) {
        init();
        long lessTimeMillis = maxWaitMillis;
        long startRequestTimeStamp = System.currentTimeMillis();
        for (; ; ) {
            //第一次尝试不能阻塞
            CrawlerSession crawlerSession = getSessionInternal(0);
            if (crawlerSession == null) {// 如果系统本身线程数不够,则使用主调线程,此方案后续讨论是否合适
                if (idleSessionQueue.size() + leaveSessions.size() < maxSize || forceCreate) {
                    crawlerSession = createNewSession();
                    //forceCreate 模式下,多做一次尝试,避免偶然因素导致session创建失败,因为此模式下代码请求需要一定实时性,需要最快的返回
                    if (forceCreate && crawlerSession == null) {
                        crawlerSession = createNewSession();
                    }
                    lessTimeMillis = maxWaitMillis > 0 ? lessTimeMillis - (System.currentTimeMillis() - startRequestTimeStamp) : maxWaitMillis;
                    if (crawlerSession == null && maxWaitMillis > 0 && lessTimeMillis > 0) {
                        /**
                         * 在等待session的资源,出现此监控,证明爬虫所需要的资源处于匮乏状态了
                         */
                        VSCrawlerMonitor.recordOne(vsCrawlerContext.getCrawlerName() + "_wait_session_resource");
                        CommonUtil.sleep(lessTimeMillis >= 1000 ? 1000 : lessTimeMillis);
                    }
                } else {
                    crawlerSession = getSessionInternal(maxWaitMillis > 0 ? lessTimeMillis : 2000);
                }
            }
            lessTimeMillis = maxWaitMillis > 0 ? lessTimeMillis - (System.currentTimeMillis() - startRequestTimeStamp) : maxWaitMillis;
            if (crawlerSession == null && lessTimeMillis <= 0 && maxWaitMillis >= 0) {
                return null;
            }
            if (crawlerSession == null) {
                continue;
            }

            // 各种check
            if (!crawlerSession.isValid()) {
                crawlerSession.destroy();
                continue;
            }

            // 单个session使用太频繁
            if (System.currentTimeMillis() - crawlerSession.getLastActiveTimeStamp() < reuseDuration) {
                // tempCrawlerSession.add(crawlerSession);
                idleSessionQueue.offer(new SessionHolder(crawlerSession));
                long sleepTimeStamp = crawlerSession.getLastActiveTimeStamp() + reuseDuration - System.currentTimeMillis();
                //并发太高,导致session资源池的资源在短时间大量被重用
                VSCrawlerMonitor.recordOne(vsCrawlerContext.getCrawlerName() + "_parallel_request_too_high");
                CommonUtil.sleep(sleepTimeStamp > 0 ? sleepTimeStamp : 100);//否则可能cpu飙高
                continue;
            }

            // 单个session使用太久了
            if (System.currentTimeMillis() - crawlerSession.getInitTimeStamp() > maxOnlineDuration) {
                crawlerSession.destroy();
                continue;
            }

            vsCrawlerContext.getAutoEventRegistry().findEventDeclaring(SessionBorrowEvent.class)
                    .onSessionBorrow(vsCrawlerContext, crawlerSession);
            if (!crawlerSession.isValid()) {
                crawlerSession.destroy();
                continue;
            }
            //记录session池的资源状态
            VSCrawlerMonitor.recordSize(vsCrawlerContext.getCrawlerName() + "_idle_session", idleSessionQueue.size());
            VSCrawlerMonitor.recordSize(vsCrawlerContext.getCrawlerName() + "_leave_session", leaveSessions.size());
//            if (log.isDebugEnabled()) {
//                log.debug("当前session数量:{}", idleSessionQueue.size() + leaveSessions.size());
//            }
            leaveSessions.add(crawlerSession);
            return crawlerSession;
        }

    }

    private CrawlerSession getSessionInternal(long maxWait) {

        if (idleSessionQueue.size() + leaveSessions.size() < coreSize) {
            try {
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                throw new PoolException("lock interrupted", e);
            }
            try {
                empty.signal();
            } finally {
                lock.unlock();
            }
        }

        try {
            if (maxWait > 0) {
                SessionHolder poll = idleSessionQueue.poll(maxWait, TimeUnit.MILLISECONDS);
                return poll == null ? null : poll.crawlerSession;
            }
            SessionHolder poll = idleSessionQueue.poll();
            return poll == null ? null : poll.crawlerSession;
        } catch (InterruptedException interruptedException) {
            throw new PoolException("lock interrupted", interruptedException);
        }

    }

    @Override
    public void crawlerEnd(VSCrawlerContext vsCrawlerContext) {
        if (sessionDaemonThread != null && !sessionDaemonThread.isInterrupted()) {
            sessionDaemonThread.interrupt();
        }
        log.info("关闭所有session....");
        for (SessionHolder crawlerSession : idleSessionQueue) {
            crawlerSession.crawlerSession.destroy();
        }
        for (CrawlerSession crawlerSession : leaveSessions) {
            crawlerSession.destroy();
        }
    }

    public int sessionNumber() {
        return leaveSessions.size() + idleSessionQueue.size();
    }

    private class SessionHolder implements Delayed {
        private CrawlerSession crawlerSession;

        SessionHolder(CrawlerSession crawlerSession) {
            this.crawlerSession = crawlerSession;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            if (crawlerSession.getLastActiveTimeStamp() == 0) {
                return 0;
            }
            long delay = crawlerSession.getLastActiveTimeStamp() + reuseDuration - System.currentTimeMillis();
            if (delay <= 0) {
                return 0;
            }
            return unit.convert(delay, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            if (o instanceof SessionHolder) {
                SessionHolder other = (SessionHolder) o;
                if (this == other) {
                    return 0;
                }
                return Long.valueOf(this.crawlerSession.getLastActiveTimeStamp())
                        .compareTo(other.crawlerSession.getLastActiveTimeStamp());
            }

            long d = (getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS));
            return (d == 0) ? 0 : ((d < 0) ? -1 : 1);
        }
    }

    private class SessionDaemonThread extends Thread {
        SessionDaemonThread() {
            super("SessionDaemonThread");
            setDaemon(true);
        }

        @Override
        public void run() {
            long initSleepTimeStamp = 2000L;
            long maxSleepTimeStamp = initSleepTimeStamp * 30;
            long sleepTimeStamp = initSleepTimeStamp;
            while (!Thread.currentThread().isInterrupted()) {
                if (leaveSessions.size() + idleSessionQueue.size() > coreSize) {
                    lock.lock();
                    try {
                        empty.await();
                    } catch (InterruptedException e) {
                        break;
                    } finally {
                        lock.unlock();
                    }
                }

                CrawlerSession newSession = createNewSession();
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                if (!createNewSessionStatus) {
                    if (sleepTimeStamp < maxSleepTimeStamp) {
                        sleepTimeStamp += 1000L;
                    }
                    CommonUtil.sleep(sleepTimeStamp);
                    continue;
                }
                sleepTimeStamp = initSleepTimeStamp;
                idleSessionQueue.add(new SessionHolder(newSession));
            }
        }
    }
}
