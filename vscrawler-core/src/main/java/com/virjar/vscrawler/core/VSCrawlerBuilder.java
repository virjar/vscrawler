package com.virjar.vscrawler.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.virjar.vscrawler.core.event.support.AutoEventRegistry;
import com.virjar.vscrawler.core.event.systemevent.SeedEmptyEvent;
import com.virjar.vscrawler.core.event.systemevent.ShutDownChecker;
import com.virjar.vscrawler.core.net.CrawlerHttpClientGenerator;
import com.virjar.vscrawler.core.net.DefaultHttpClientGenerator;
import com.virjar.vscrawler.core.net.proxy.IPPool;
import com.virjar.vscrawler.core.net.proxy.strategy.ProxyPlanner;
import com.virjar.vscrawler.core.net.proxy.strategy.ProxyStrategy;
import com.virjar.vscrawler.core.net.session.CrawlerSessionPool;
import com.virjar.vscrawler.core.net.session.LoginHandler;
import com.virjar.vscrawler.core.net.user.*;
import com.virjar.vscrawler.core.processor.BindRouteProcessor;
import com.virjar.vscrawler.core.processor.PageDownLoadProcessor;
import com.virjar.vscrawler.core.processor.RouteProcessor;
import com.virjar.vscrawler.core.processor.SeedProcessor;
import com.virjar.vscrawler.core.resourcemanager.ResourceManager;
import com.virjar.vscrawler.core.resourcemanager.ResourceManagerFactory;
import com.virjar.vscrawler.core.resourcemanager.model.ResourceSetting;
import com.virjar.vscrawler.core.resourcemanager.service.QueueStore;
import com.virjar.vscrawler.core.resourcemanager.service.RamQueueStore;
import com.virjar.vscrawler.core.resourcemanager.service.ResourceQueue;
import com.virjar.vscrawler.core.seed.*;
import com.virjar.vscrawler.core.serialize.ConsolePipeline;
import com.virjar.vscrawler.core.serialize.Pipeline;
import com.virjar.vscrawler.core.util.VSCrawlerConstant;

import java.util.List;
import java.util.Set;

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
    private List<BindRouteProcessor> seedRouters = Lists.newLinkedList();

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
    private int workerThreadNumber = Runtime.getRuntime().availableProcessors() * 4;

    /**
     * 慢启动控制
     */
    private boolean slowStart = false;

    /**
     * 慢启动时长
     */
    private long slowStartDuration = 5 * 60 * 1000;

    /**
     * 一分钟内没有新任务,则关闭爬虫
     */
    private long stopWhileTaskEmptyDuration = 60 * 1000;

    private String crawlerName = VSCrawlerConstant.DEFAULT_CRAWLER_NAME;

    private ResourceManager resourceManager;

    private QueueStore defaultQueueStore;

    private ResourceSetting defaultResourceSetting;

    private Set<Object> eventObservers = Sets.newHashSet();

    /**
     * 自动创建session
     */
    private boolean autoCreateSession = true;

    public static VSCrawlerBuilder create() {
        return new VSCrawlerBuilder();
    }

    public VSCrawlerBuilder setStopWhileTaskEmptyDuration(long stopWhileTaskEmptyDuration) {
        this.stopWhileTaskEmptyDuration = stopWhileTaskEmptyDuration;
        return this;
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

    public VSCrawlerBuilder addRouteProcessor(BindRouteProcessor seedRouter) {
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

    public VSCrawlerBuilder setCrawlerName(String crawlerName) {
        if (crawlerName == null || !crawlerName.matches("[0-9a-zA-Z_]+")) {
            throw new IllegalArgumentException("crawlerName must be identified,for :" + crawlerName);
        }
        this.crawlerName = crawlerName;
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

    public VSCrawlerBuilder setResourceManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        return this;
    }

    public VSCrawlerBuilder setDefaultQueueStore(QueueStore defaultQueueStore) {
        this.defaultQueueStore = defaultQueueStore;
        return this;
    }

    public VSCrawlerBuilder setDefaultResourceSetting(ResourceSetting defaultResourceSetting) {
        this.defaultResourceSetting = defaultResourceSetting;
        return this;
    }

    public VSCrawlerBuilder addEventObserver(Object observer) {
        this.eventObservers.add(observer);
        return this;
    }


    public VSCrawlerBuilder autoCreateSession(boolean autoCreateSession) {
        this.autoCreateSession = autoCreateSession;
        return this;
    }

    public VSCrawler build() {

        final VSCrawlerContext vsCrawlerContext = VSCrawlerContext.create(crawlerName);

        if (crawlerHttpClientGenerator == null) {
            crawlerHttpClientGenerator = new DefaultHttpClientGenerator();
        }

        if (proxyStrategy == null) {
            proxyStrategy = ProxyStrategy.NONE;
        }

        if (proxyStrategy == ProxyStrategy.CUSTOM && proxyPlanner == null) {
            throw new IllegalStateException("proxyPlanner must exist if proxyStrategy is custom");
        }

        CrawlerSessionPool crawlerSessionPool = new CrawlerSessionPool(vsCrawlerContext, crawlerHttpClientGenerator, proxyStrategy,
                ipPool, proxyPlanner, sessionPoolMaxSize, sessionPoolCoreSize, sessionPoolInitialSize,
                sessionPoolReuseDuration, sessionPoolMaxOnlineDuration, autoCreateSession);

        if (initSeedSource == null) {
            initSeedSource = new LocalFileSeedSource();
        }

        if (seedKeyResolver == null) {
            seedKeyResolver = new DefaultSeedKeyResolver();
        }

        if (segmentResolver == null) {
            segmentResolver = new DefaultSegmentResolver();
        }

        BerkeleyDBSeedManager berkeleyDBSeedManager = new BerkeleyDBSeedManager(vsCrawlerContext, initSeedSource, seedKeyResolver,
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

        VSCrawler vsCrawler = new VSCrawler(vsCrawlerContext, crawlerSessionPool, berkeleyDBSeedManager, processor, pipelineList,
                workerThreadNumber, slowStart, slowStartDuration);
        if (loginOnSessionCreate) {
            if (userResourceFacade == null) {
                userResourceFacade = new DefaultUserResource();
            }
        }

        if (resourceManager == null) {
            resourceManager = ResourceManagerFactory.create().build();
        }
        vsCrawlerContext.setResourceManager(resourceManager);
        if (defaultQueueStore == null) {
            defaultQueueStore = new RamQueueStore();
        }
        vsCrawlerContext.setQueueStore(defaultQueueStore);
        if (defaultResourceSetting == null) {
            defaultResourceSetting = ResourceSetting.create().setLock(true);
        }
        vsCrawlerContext.setResourceSetting(defaultResourceSetting);

        if (userResourceFacade != null) {
            if (loginHandler == null) {
                throw new IllegalStateException("login handler is null ,but open login switch");
            }
            ResourceQueue resourceQueue = resourceManager.getResourceQueue(vsCrawlerContext.makeUserResourceTag());
            if (resourceQueue != null) {
                resourceQueue.addResourceLoader(new UserManager2ResourceLoader(userResourceFacade));
            } else {
                resourceManager.registry(new ResourceQueue(vsCrawlerContext.makeUserResourceTag(), defaultQueueStore, defaultResourceSetting, new UserManager2ResourceLoader(userResourceFacade)));
            }
            addEventObserver(new AutoLoginPlugin(loginHandler, new UserManager2(resourceManager, vsCrawlerContext)));
        }

        if (stopWhileTaskEmptyDuration > 0) {
            final VSCrawler finalVSCrawler = vsCrawler;
            addEventObserver(new ShutDownChecker() {

                @Override
                public void checkShutDown(VSCrawlerContext vsCrawlerContext1) {
                    // 15s之后检查活跃线程数,发现为0,证明连续10s都没用任务执行了
                    if (finalVSCrawler.activeWorker() == 0
                            && (System.currentTimeMillis() - finalVSCrawler.getLastActiveTime()) > 10000) {
                        System.out.println((stopWhileTaskEmptyDuration / 1000) + "秒没收到爬虫任务,自动爬虫关闭器,尝试停止爬虫");
                        finalVSCrawler.stopCrawler();
                    }
                }
            });
            addEventObserver(new SeedEmptyEvent() {
                @Override
                public void onSeedEmpty(VSCrawlerContext vsCrawlerContext1) {
                    finalVSCrawler.getVsCrawlerContext().getAutoEventRegistry().createDelayEventSender(ShutDownChecker.class,
                            stopWhileTaskEmptyDuration).delegate()
                            .checkShutDown(vsCrawlerContext);
                }
            });
        }

        if (eventObservers.size() > 0) {
            vsCrawler.addCrawlerStartCallBack(new VSCrawler.CrawlerStartCallBack() {
                @Override
                public void onCrawlerStart(VSCrawler vsCrawler) {
                    AutoEventRegistry autoEventRegistry = vsCrawler.getVsCrawlerContext().getAutoEventRegistry();
                    for (Object eventObserver : eventObservers) {
                        autoEventRegistry.registerObserver(eventObserver);
                    }
                }
            });
            for (Object observer : eventObservers) {
                if (observer instanceof VSCrawler.CrawlerStartCallBack) {
                    vsCrawler.addCrawlerStartCallBack((VSCrawler.CrawlerStartCallBack) observer);
                }
            }
        }

        return vsCrawler;
    }
}
