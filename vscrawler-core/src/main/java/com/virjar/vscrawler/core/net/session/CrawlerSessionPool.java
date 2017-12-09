package com.virjar.vscrawler.core.net.session;

import com.google.common.collect.Sets;
import com.virjar.dungproxy.client.util.CommonUtil;
import com.virjar.vscrawler.core.VSCrawlerContext;
import com.virjar.vscrawler.core.event.systemevent.CrawlerEndEvent;
import com.virjar.vscrawler.core.event.systemevent.SessionBorrowEvent;
import com.virjar.vscrawler.core.event.systemevent.SessionCreateEvent;
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
    private DelayQueue<SessionHolder> sessionQueue = new DelayQueue<>();
    private Set<CrawlerSession> runningSessions = Sets.newConcurrentHashSet();

    @Getter
    private VSCrawlerContext vsCrawlerContext;

    public CrawlerSessionPool(VSCrawlerContext vsCrawlerContext, CrawlerHttpClientGenerator crawlerHttpClientGenerator, ProxyStrategy proxyStrategy,
                              IPPool ipPool, ProxyPlanner proxyPlanner, int maxSize, int coreSize, int initialSize, long reuseDuration,
                              long maxOnlineDuration) {
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
                        sessionQueue.offer(new SessionHolder(newSession));
                    }
                    if (totalTry > initialSize * 3) {
                        throw new IllegalStateException("can not create session ,all session create failed");
                    }
                }
            }

            sessionDaemonThread = new SessionDaemonThread();
            sessionDaemonThread.start();
        } finally {
            inited = true;
            lock.unlock();
        }
    }

    private CrawlerSession createNewSession() {
        CrawlerSession crawlerSession = new CrawlerSession(crawlerHttpClientGenerator, proxyStrategy, ipPool,
                proxyPlanner, this);
        vsCrawlerContext.getAutoEventRegistry().findEventDeclaring(SessionCreateEvent.class)
                .onSessionCreateEvent(vsCrawlerContext, crawlerSession);
        if (crawlerSession.isValid()) {
            crawlerSession.setInitTimeStamp(System.currentTimeMillis());
            createNewSessionStatus = true;
            return crawlerSession;
        }
        createNewSessionStatus = false;
        return null;
    }

    public void recycle(CrawlerSession crawlerSession) {
        runningSessions.remove(crawlerSession);
        crawlerSession.setLastActiveTimeStamp(System.currentTimeMillis());
        if (sessionQueue.size() > maxSize || !crawlerSession.isValid()) {
            crawlerSession.destroy();
        } else {
            sessionQueue.offer(new SessionHolder(crawlerSession));
        }
    }

    public CrawlerSession borrowOne(long maxWaitMillis, boolean forceCreate) {
        init();
        long lessTimeMillis = maxWaitMillis;
        // LinkedList<CrawlerSession> tempCrawlerSession = Lists.newLinkedList();

        // logger.info("当前会话池中,共有:{}个用户可用", allSessions.size());

        long startRequestTimeStamp = System.currentTimeMillis();
        for (; ; ) {
            CrawlerSession crawlerSession = getSessionInternal(lessTimeMillis);
            if (crawlerSession == null) {// 如果系统本身线程数不够,则使用主调线程,此方案后续讨论是否合适
                if (sessionQueue.size() + runningSessions.size() < maxSize || forceCreate) {
                    crawlerSession = createNewSession();
                    //forceCreate 模式下,多做一次尝试,避免偶然因素导致session创建失败,因为此模式下代码请求需要一定实时性,需要最快的返回
                    if (forceCreate && crawlerSession == null) {
                        crawlerSession = createNewSession();
                    }
                    if (crawlerSession == null) {
                        CommonUtil.sleep(2000);
                    }
                } else {
                    crawlerSession = getSessionInternal(2000);
                }
            }
            if (crawlerSession == null && lessTimeMillis < 0 && maxWaitMillis > 0) {
                return null;
            }
            lessTimeMillis = maxWaitMillis > 0 ? lessTimeMillis - (System.currentTimeMillis() - startRequestTimeStamp) : maxWaitMillis;
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
                sessionQueue.offer(new SessionHolder(crawlerSession));
                CommonUtil.sleep(2000);//否则可能cpu飙高
                continue;
            }

            // 单个session使用太久了
            if (System.currentTimeMillis() - crawlerSession.getInitTimeStamp() > maxOnlineDuration) {
                crawlerSession.destroy();
                continue;
            }

            vsCrawlerContext.getAutoEventRegistry().findEventDeclaring(SessionBorrowEvent.class)
                    .onSessionBorrow(vsCrawlerContext, crawlerSession);
            log.debug("当前session数量:{}", sessionQueue.size() + runningSessions.size());
            runningSessions.add(crawlerSession);
            return crawlerSession;
        }

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

    private CrawlerSession getSessionInternal(long maxWait) {

        if (sessionQueue.size() + runningSessions.size() < coreSize) {
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
                SessionHolder poll = sessionQueue.poll(maxWait, TimeUnit.MILLISECONDS);
                return poll == null ? null : poll.crawlerSession;
            } else if (sessionQueue.size() + runningSessions.size() >= maxSize) {
                return sessionQueue.take().crawlerSession;
            } else {
                SessionHolder poll = sessionQueue.poll();
                return poll == null ? null : poll.crawlerSession;
            }
        } catch (InterruptedException interruptedException) {
            throw new PoolException("lock interrupted", interruptedException);
        }

    }

    @Override
    public void crawlerEnd(VSCrawlerContext vsCrawlerContext) {
        sessionDaemonThread.interrupt();
    }

    private volatile boolean createNewSessionStatus = true;

    private class SessionDaemonThread extends Thread {
        SessionDaemonThread() {
            super("SessionDaemonThread");
            setDaemon(true);
        }

        @Override
        public void run() {
            long initSleepTimeStamp = 2000L;
            long maxSleepTimeStamp = initSleepTimeStamp * 15;
            long sleepTimeStamp = initSleepTimeStamp;
            while (!Thread.currentThread().isInterrupted()) {
                if (runningSessions.size() + sessionQueue.size() > maxSize) {
                    lock.lock();
                    try {
                        empty.await();
                    } catch (InterruptedException e) {
                        log.warn("wait interrupted", e);
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
                sessionQueue.add(new SessionHolder(newSession));
            }
        }
    }
}
