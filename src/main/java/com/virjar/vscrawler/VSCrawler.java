package com.virjar.vscrawler;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.math.NumberUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.virjar.dungproxy.client.ningclient.concurrent.NamedThreadFactory;
import com.virjar.dungproxy.client.util.CommonUtil;
import com.virjar.vscrawler.event.EventLoop;
import com.virjar.vscrawler.event.support.AutoEventRegistry;
import com.virjar.vscrawler.event.systemevent.*;
import com.virjar.vscrawler.net.session.CrawlerSession;
import com.virjar.vscrawler.net.session.CrawlerSessionPool;
import com.virjar.vscrawler.processor.CrawlResult;
import com.virjar.vscrawler.processor.SeedProcessor;
import com.virjar.vscrawler.seed.BerkeleyDBSeedManager;
import com.virjar.vscrawler.seed.Seed;
import com.virjar.vscrawler.serialize.ConsolePipeline;
import com.virjar.vscrawler.serialize.Pipeline;
import com.virjar.vscrawler.util.SingtonObjectHolder;
import com.virjar.vscrawler.util.VSCrawlerConstant;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by virjar on 17/4/16. <br/>
 * 爬虫入口,目前很多逻辑参考了webmagic
 * 
 * @author virjar
 * @since 0.0.1
 */
@Slf4j
public class VSCrawler extends Thread implements CrawlerConfigChangeEvent, FirstSeedPushEvent {

    private CrawlerSessionPool crawlerSessionPool;
    // private SimpleFileSeedManager simpleFileSeedManager;
    private BerkeleyDBSeedManager berkeleyDBSeedManager;
    private SeedProcessor seedProcessor;
    private List<Pipeline> pipeline = Lists.newArrayList();
    private int threadNumber = 10;

    protected ThreadPoolExecutor threadPool;
    private Date startTime;

    protected AtomicInteger stat = new AtomicInteger(STAT_INIT);

    protected final static int STAT_INIT = 0;

    protected final static int STAT_RUNNING = 1;

    protected final static int STAT_STOPPED = 2;

    protected boolean exitWhenComplete = false;

    private ReentrantLock taskDispatchLock = new ReentrantLock();

    private Condition taskDispatchCondition = taskDispatchLock.newCondition();

    /**
     * 慢启动,默认为true,慢启动打开后,爬虫启动的时候线程不会瞬间变到最大,否则这个时候并发应该是最大的,因为这个时候没有线程阻塞, 另外考虑有些 资源分配问题,慢启动避免初始化的时候初始化资源请求qps过高
     */
    protected boolean slowStart = false;

    /**
     * 慢启动过程是10分钟默认
     */
    protected long slowStartDuration = 5 * 60 * 1000;

    private int slowStartTimes = 0;

    VSCrawler(CrawlerSessionPool crawlerSessionPool, BerkeleyDBSeedManager berkeleyDBSeedManager,
            SeedProcessor seedProcessor, List<Pipeline> pipeline) {
        super("VSCrawler-Dispatch");
        this.crawlerSessionPool = crawlerSessionPool;
        this.berkeleyDBSeedManager = berkeleyDBSeedManager;
        this.seedProcessor = seedProcessor;
        this.pipeline = pipeline;
    }

    public void stopCrawler() {
        if (stat.compareAndSet(STAT_RUNNING, STAT_STOPPED)) {
            log.info("爬虫停止,发送爬虫停止事件消息:com.virjar.vscrawler.event.systemevent.CrawlerEndEvent");
            AutoEventRegistry.getInstance().findEventDeclaring(CrawlerEndEvent.class).crawlerEnd();
        } else {
            log.info("爬虫已经停止,不需要发生爬虫停止事件消息");
        }
    }

    public void pushSeed(Seed seed) {
        this.berkeleyDBSeedManager.addNewSeeds(Lists.newArrayList(seed));
    }

    public void pushSeed(String seed) {
        berkeleyDBSeedManager.addNewSeeds(Lists.<Seed> newArrayList(new Seed(seed)));
    }

    private AtomicInteger activeTasks = new AtomicInteger(0);

    @Override
    public void run() {
        checkRunningStat();
        initComponent();
        log.info("Spider  started!");
        while (!Thread.currentThread().isInterrupted() && stat.get() == STAT_RUNNING) {
            // final String request = simpleFileSeedManager.consumeSeed();
            final Seed seed = berkeleyDBSeedManager.pool();
            if (seed == null) {
                AutoEventRegistry.getInstance().findEventDeclaring(SeedEmptyEvent.class).onSeedEmpty();
                if (threadPool.getActiveCount() == 0 && exitWhenComplete) {
                    break;
                }
                taskDispatchLock.lock();
                try {// 没有任务的时候,暂停
                    log.info("没有任务,暂停");
                    taskDispatchCondition.await();
                } catch (InterruptedException e) {
                    log.warn("爬虫线程休眠被打断", e);
                    break;
                } finally {
                    taskDispatchLock.unlock();
                }

            } else {
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            activeTasks.incrementAndGet();
                            processRequest(seed);
                        } catch (Exception e) {
                            log.error("process request {} error", JSONObject.toJSONString(seed), e);
                        } finally {
                            if (activeTasks.decrementAndGet() < threadPool.getMaximumPoolSize()) {

                                try {
                                    taskDispatchLock.lock();
                                    taskDispatchCondition.signalAll();
                                } finally {
                                    taskDispatchLock.unlock();
                                }
                            }
                        }
                    }
                });
                // 当任务满的时候,暂时阻塞任务产生线程,直到有空闲线程资源
                if (activeTasks.get() >= threadPool.getMaximumPoolSize()) {
                    try {
                        taskDispatchLock.lock();
                        taskDispatchCondition.await();
                    } catch (InterruptedException e) {
                        log.warn("爬虫线程休眠被打断", e);
                        break;
                    } finally {
                        taskDispatchLock.unlock();
                    }
                }
                if (slowStart && slowStartTimes < threadNumber - 1) {
                    log.info("慢启动");
                    CommonUtil.sleep(slowStartDuration / threadNumber);
                    slowStartTimes++;
                }
            }
        }
        stat.set(STAT_STOPPED);
        log.info("爬虫结束");
    }

    private void processRequest(Seed request) {

        CrawlerSession session = null;
        while (true) {
            // 暂时死循环等待,对于一个完善产品不应该这样
            // 从session池里面获取一个session,如果需要登录,那么得到的session必然是登录成功的
            session = crawlerSessionPool.borrowOne();
            if (session != null) {
                break;
            }
            CommonUtil.sleep(500);
        }
        int originRetryCount = request.getRetry();
        CrawlResult crawlResult = new CrawlResult();
        try {
            seedProcessor.process(request, session, crawlResult);
            if (originRetryCount != request.getRetry()) {
                if (request.getRetry() > request.getMaxRetry()) {
                    request.setStatus(Seed.STATUS_FAILED);
                } else {
                    request.setStatus(Seed.STATUS_RETRY);
                }
            } else if (!request.isIgnore()) {
                request.setStatus(Seed.STATUS_SUCCESS);// 没有发生异常,没有手动指定重试,没有指定忽略,则认为成功
            }
        } catch (Exception e) {// 如果发生了异常,并且用户没有主动重试,强制重试
            if (originRetryCount != request.getRetry()) {
                request.retry();
                if (request.getRetry() > request.getMaxRetry()) {
                    request.setStatus(Seed.STATUS_FAILED);
                } else {
                    request.setStatus(Seed.STATUS_RETRY);
                }
            }
        } finally {
            // 归还一个session,session有并发控制,feedback之后session才能被其他任务复用
            // 如果标记session失效,则会停止分发此session,同时异步触发登录逻辑
            berkeleyDBSeedManager.updateSeed(request);
        }

        List<Seed> seeds = crawlResult.allSeed();
        if (seeds != null) {
            berkeleyDBSeedManager.addNewSeeds(seeds);
        }

        List<String> allResult = crawlResult.allResult();
        if (allResult != null) {
            for (Pipeline p : pipeline) {
                p.saveItem(allResult, request);
            }
        }
    }

    private void checkRunningStat() {
        while (true) {
            int statNow = stat.get();
            if (statNow == STAT_RUNNING) {
                throw new IllegalStateException("Spider is already running!");
            }
            if (stat.compareAndSet(statNow, STAT_RUNNING)) {
                break;
            }
        }
    }

    @Override
    public void configChange(Properties oldProperties, Properties newProperties) {
        config(newProperties);
    }

    private void config(Properties properties) {
        // 事件循环是单线程的,所以设计上来说,不会有并发问题
        int newThreadNumber = NumberUtils.toInt(properties.getProperty(VSCrawlerConstant.VSCRAWLER_THREAD_NUMBER));
        if (newThreadNumber != threadNumber) {
            log.info("爬虫线程数目变更,由:{}  变化为:{}", threadNumber, newThreadNumber);
            threadPool.setCorePoolSize(newThreadNumber);
            threadPool.setMaximumPoolSize(newThreadNumber);
            threadNumber = newThreadNumber;
        }
    }

    void initComponent() {

        // 开启事件循环
        EventLoop.getInstance().loop();

        // 开启文件监听,并发送初始化配置事件
        SingtonObjectHolder.vsCrawlerConfigFileWatcher.watchAndBindEvent();

        // config 会设置 threadPool
        if (threadPool == null || threadPool.isShutdown()) {
            threadPool = new ThreadPoolExecutor(threadNumber, threadNumber, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory("VSCrawlerWorker", false));

        }

        // 加载初始化配置
        config(SingtonObjectHolder.vsCrawlerConfigFileWatcher.loadedProperties());

        // 让本类监听配置文件变更事件
        AutoEventRegistry.getInstance().registerObserver(this);

        if (pipeline.size() == 0) {
            pipeline.add(new ConsolePipeline());
        }

        startTime = new Date();

        berkeleyDBSeedManager.init();

        AutoEventRegistry.getInstance().findEventDeclaring(CrawlerStartEvent.class).onCrawlerStart();

        // 如果爬虫是强制停止的,比如kill -9,那么尝试发送爬虫停止信号,请注意
        // 一般请求请正常停止程序,关机拦截这是挽救方案,并不一定可以完整的实现收尾方案
        Runtime.getRuntime().addShutdownHook(new ResourceCleanHookThread());

    }

    private class ResourceCleanHookThread extends Thread {
        ResourceCleanHookThread() {
            super("vsCrawler-resource-clean");
        }

        @Override
        public void run() {
            log.warn("爬虫被外部中断,尝试进行资源关闭等收尾工作");
            VSCrawler.this.stopCrawler();
        }
    }

    @Override
    public void firstSeed(Seed seed) {
        log.info("新的种子加入,激活爬虫派发线程");
        try {
            taskDispatchLock.lock();
            taskDispatchCondition.signalAll();
        } finally {
            taskDispatchLock.unlock();
        }
    }
}
