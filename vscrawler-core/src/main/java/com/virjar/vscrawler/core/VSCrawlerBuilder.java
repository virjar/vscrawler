package com.virjar.vscrawler.core;

import java.util.List;

import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.net.CrawlerHttpClientGenerator;
import com.virjar.vscrawler.core.net.DefaultHttpClientGenerator;
import com.virjar.vscrawler.core.net.proxy.IPPool;
import com.virjar.vscrawler.core.net.proxy.strategy.ProxyPlanner;
import com.virjar.vscrawler.core.net.proxy.strategy.ProxyStrategy;
import com.virjar.vscrawler.core.net.session.CrawlerSessionPool;
import com.virjar.vscrawler.core.net.session.LoginHandler;
import com.virjar.vscrawler.core.net.user.AutoLoginPlugin;
import com.virjar.vscrawler.core.net.user.DefaultUserResource;
import com.virjar.vscrawler.core.net.user.UserManager;
import com.virjar.vscrawler.core.net.user.UserResourceFacade;
import com.virjar.vscrawler.core.processor.PageDownLoadProcessor;
import com.virjar.vscrawler.core.processor.RouteProcessor;
import com.virjar.vscrawler.core.processor.SeedProcessor;
import com.virjar.vscrawler.core.processor.SeedRouter;
import com.virjar.vscrawler.core.seed.*;
import com.virjar.vscrawler.core.serialize.ConsolePipeline;
import com.virjar.vscrawler.core.serialize.Pipeline;

/**
 * Created by virjar on 17/4/30.<br/>
 * build a crawlerInstance
 *
 * @author virjar
 * @since 0.0.1
 */
public class VSCrawlerBuilder {
    /**
     * httpclient构造器,可能需要定制自己的httpclient
     */
    private CrawlerHttpClientGenerator crawlerHttpClientGenerator;

    /**
     * 登录处理器
     */
    private LoginHandler loginHandler;

    /**
     * 用户数据导入源
     */
    private UserResourceFacade userResourceFacade;

    /**
     * 代理切换策略
     */
    private ProxyStrategy proxyStrategy;

    /**
     * 代理池对象
     */
    private IPPool ipPool;

    /**
     * 自定义代理策略的时候,代理决策器
     */
    private ProxyPlanner proxyPlanner;

    /**
     * 种子处理器,负责解析页面逻辑
     */
    private SeedProcessor processor;

    /**
     * 基于路由的页面解析器
     */
    private List<SeedRouter> seedRouters = Lists.newLinkedList();

    /**
     * 序列化层
     */
    private List<Pipeline> pipelineList = Lists.newLinkedList();

    /**
     * 初始化种子来源
     */
    private InitSeedSource initSeedSource;

    /**
     * 种子ID决策器,他的存在可以提供自定义消重功能
     */
    private SeedKeyResolver seedKeyResolver;

    /**
     * 段决策器,实现种子按时间分段,分段后段内种子消重,段间种子互不消重
     */
    private SegmentResolver segmentResolver;

    private boolean loginOnSessionCreate = false;

    /**
     * session池,初始化大小
     */
    private int sessionPoolInitialSize = 0;

    /**
     * session池,核心大小
     */
    private int sessionPoolCoreSize = 5;

    /**
     * session池,最大大小
     */
    private int sessionPoolMaxSize = 30;

    /**
     * session池,重用时间间隔,太短了可能被封
     */
    private long sessionPoolReuseDuration = 0L;

    /**
     * session池,seesion最大在线时长,太长了则可能封
     */
    private long sessionPoolMaxOnlineDuration = Long.MAX_VALUE;

    private int seedManagerCacheSize = 1024;

    /**
     * 爬虫工作线程数
     */
    private int workerThreadNumber = 10;

    /**
     * 慢启动控制
     */
    private boolean slowStart = false;

    /**
     * 慢启动时长
     */
    private long slowStartDuration = 5 * 60 * 1000;

    public static VSCrawlerBuilder create() {
        return new VSCrawlerBuilder();
    }

    public VSCrawlerBuilder setSlowStart(boolean slowStart) {
        this.slowStart = slowStart;
        return this;
    }

    public VSCrawlerBuilder setSlowStartDuration(long slowStartDuration) {
        this.slowStartDuration = slowStartDuration;
        return this;
    }

    public VSCrawlerBuilder setWorkerThreadNumber(int workerThreadNumber) {
        this.workerThreadNumber = workerThreadNumber;
        return this;
    }

    public VSCrawlerBuilder setLoginOnSessionCreate(boolean loginOnSessionCreate) {
        this.loginOnSessionCreate = loginOnSessionCreate;
        return this;
    }

    public VSCrawlerBuilder setCrawlerHttpClientGenerator(CrawlerHttpClientGenerator crawlerHttpClientGenerator) {
        this.crawlerHttpClientGenerator = crawlerHttpClientGenerator;
        return this;
    }

    public VSCrawlerBuilder setInitSeedSource(InitSeedSource initSeedSource) {
        this.initSeedSource = initSeedSource;
        return this;
    }

    public VSCrawlerBuilder setIpPool(IPPool ipPool) {
        this.ipPool = ipPool;
        return this;
    }

    public VSCrawlerBuilder setLoginHandler(LoginHandler loginHandler) {
        this.loginHandler = loginHandler;
        return this;
    }

    public VSCrawlerBuilder setPipelineList(List<Pipeline> pipelineList) {
        this.pipelineList = pipelineList;
        return this;
    }

    public VSCrawlerBuilder addPipeline(Pipeline pipeline) {
        this.pipelineList.add(pipeline);
        return this;
    }

    public VSCrawlerBuilder setProcessor(SeedProcessor processor) {
        this.processor = processor;
        return this;
    }

    public VSCrawlerBuilder addRouteProcessor(SeedRouter seedRouter) {
        this.seedRouters.add(seedRouter);
        return this;
    }

    public VSCrawlerBuilder setProxyPlanner(ProxyPlanner proxyPlanner) {
        this.proxyPlanner = proxyPlanner;
        return this;
    }

    public VSCrawlerBuilder setProxyStrategy(ProxyStrategy proxyStrategy) {
        this.proxyStrategy = proxyStrategy;
        return this;
    }

    public VSCrawlerBuilder setSeedKeyResolver(SeedKeyResolver seedKeyResolver) {
        this.seedKeyResolver = seedKeyResolver;
        return this;
    }

    public VSCrawlerBuilder setSegmentResolver(SegmentResolver segmentResolver) {
        this.segmentResolver = segmentResolver;
        return this;
    }

    public VSCrawlerBuilder setUserResourceFacade(UserResourceFacade userResourceFacade) {
        this.userResourceFacade = userResourceFacade;
        return this;
    }

    public VSCrawlerBuilder setSessionPoolCoreSize(int sessionPoolCoreSize) {
        this.sessionPoolCoreSize = sessionPoolCoreSize;

        if (sessionPoolMaxSize < sessionPoolCoreSize) {
            sessionPoolMaxSize = sessionPoolCoreSize;
        }
        if (sessionPoolInitialSize > sessionPoolMaxSize) {
            sessionPoolInitialSize = sessionPoolMaxSize;
        }
        return this;
    }

    public VSCrawlerBuilder setSessionPoolInitialSize(int sessionPoolInitialSize) {
        this.sessionPoolInitialSize = sessionPoolInitialSize;

        if (sessionPoolInitialSize > sessionPoolMaxSize) {
            sessionPoolMaxSize = sessionPoolInitialSize;
        }
        if (sessionPoolCoreSize > sessionPoolMaxSize) {
            sessionPoolCoreSize = sessionPoolMaxSize;
        }
        return this;
    }

    public VSCrawlerBuilder setSessionPoolMaxOnlineDuration(long sessionPoolMaxOnlineDuration) {
        this.sessionPoolMaxOnlineDuration = sessionPoolMaxOnlineDuration;
        return this;
    }

    public VSCrawlerBuilder setSessionPoolMaxSize(int sessionPoolMaxSize) {
        this.sessionPoolMaxSize = sessionPoolMaxSize;

        if (sessionPoolCoreSize > sessionPoolMaxSize) {
            sessionPoolCoreSize = sessionPoolMaxSize;
        }
        if (sessionPoolInitialSize > sessionPoolMaxSize) {
            sessionPoolInitialSize = sessionPoolMaxSize;
        }
        return this;
    }

    public VSCrawlerBuilder setSessionPoolReuseDuration(long sessionPoolReuseDuration) {
        this.sessionPoolReuseDuration = sessionPoolReuseDuration;
        return this;
    }

    public VSCrawlerBuilder setSeedManagerCacheSize(int seedManagerCacheSize) {
        this.seedManagerCacheSize = seedManagerCacheSize;
        return this;
    }

    public VSCrawler build() {

        if (crawlerHttpClientGenerator == null) {
            crawlerHttpClientGenerator = new DefaultHttpClientGenerator();
        }

        if (proxyStrategy == null) {
            proxyStrategy = ProxyStrategy.NONE;
        }

        if (proxyStrategy == ProxyStrategy.CUSTOM && proxyPlanner == null) {
            throw new IllegalStateException("proxyPlanner must exist if proxyStrategy is custom");
        }

        CrawlerSessionPool crawlerSessionPool = new CrawlerSessionPool(crawlerHttpClientGenerator, proxyStrategy,
                ipPool, proxyPlanner, sessionPoolMaxSize, sessionPoolCoreSize, sessionPoolInitialSize,
                sessionPoolReuseDuration, sessionPoolMaxOnlineDuration);

        if (initSeedSource == null) {
            initSeedSource = new LocalFileSeedSource();
        }

        if (seedKeyResolver == null) {
            seedKeyResolver = new DefaultSeedKeyResolver();
        }

        if (segmentResolver == null) {
            segmentResolver = new DefaultSegmentResolver();
        }

        BerkeleyDBSeedManager berkeleyDBSeedManager = new BerkeleyDBSeedManager(initSeedSource, seedKeyResolver,
                segmentResolver, seedManagerCacheSize);

        if (processor == null && seedRouters.isEmpty()) {
            processor = new PageDownLoadProcessor();
        }

        if (processor != null && !seedRouters.isEmpty()) {
            throw new IllegalStateException(" seedProcessor and routeProcessor conflict");
        }

        if (!seedRouters.isEmpty()) {
            RouteProcessor routeProcessor = new RouteProcessor();
            routeProcessor.addRouters(seedRouters);
            processor = routeProcessor;
        }

        if (pipelineList.isEmpty()) {
            pipelineList.add(new ConsolePipeline());
        }

        VSCrawler vsCrawler = new VSCrawler(crawlerSessionPool, berkeleyDBSeedManager, processor, pipelineList,
                workerThreadNumber, slowStart, slowStartDuration);
        if (loginOnSessionCreate) {
            if (userResourceFacade == null) {
                userResourceFacade = new DefaultUserResource();
            }
        }
        if (userResourceFacade != null) {
            if (loginHandler == null) {
                throw new IllegalStateException("login handler is null ,but open login switch");
            }
            UserManager userManager = new UserManager(userResourceFacade);
            vsCrawler.addCrawlerStartCallBack(new AutoLoginPlugin(loginHandler, userManager));
        }

        return vsCrawler;
    }
}
