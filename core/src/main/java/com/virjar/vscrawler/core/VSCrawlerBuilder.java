package com.virjar.vscrawler.core;

import java.util.List;

import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.net.CrawlerHttpClientGenerator;
import com.virjar.vscrawler.core.net.DefaultHttpClientGenerator;
import com.virjar.vscrawler.core.net.proxy.IPPool;
import com.virjar.vscrawler.core.net.proxy.strategy.ProxyPlanner;
import com.virjar.vscrawler.core.net.proxy.strategy.ProxyStrategy;
import com.virjar.vscrawler.core.net.session.CrawlerSessionPool;
import com.virjar.vscrawler.core.net.session.EmptyLoginHandler;
import com.virjar.vscrawler.core.net.session.LoginHandler;
import com.virjar.vscrawler.core.net.user.DefaultUserResource;
import com.virjar.vscrawler.core.net.user.UserManager;
import com.virjar.vscrawler.core.net.user.UserResourceFacade;
import com.virjar.vscrawler.core.processor.PageDownLoadProcessor;
import com.virjar.vscrawler.core.processor.SeedProcessor;
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
     * 序列化层
     */
    private List<Pipeline> pipelineList = Lists.newArrayList();

    /**
     * 初始化种子来源
     */
    private InitSeedSource initSeedSource;

    /**
     * 种子ID决策器,他的存在可以提供自定义消重功能
     */
    private SeedKeyResolver seedKeyResolver;

    public static VSCrawlerBuilder create() {
        return new VSCrawlerBuilder();
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

    public VSCrawlerBuilder addPipeline(Pipeline pipeline){
        this.pipelineList.add(pipeline);
        return this;
    }

    public VSCrawlerBuilder setProcessor(SeedProcessor processor) {
        this.processor = processor;
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

    public VSCrawlerBuilder setUserResourceFacade(UserResourceFacade userResourceFacade) {
        this.userResourceFacade = userResourceFacade;
        return this;
    }

    public VSCrawler build() {
        if (userResourceFacade == null) {
            userResourceFacade = new DefaultUserResource();
        }
        UserManager userManager = new UserManager(userResourceFacade);

        if (loginHandler == null) {
            loginHandler = new EmptyLoginHandler();
        }

        if (crawlerHttpClientGenerator == null) {
            crawlerHttpClientGenerator = new DefaultHttpClientGenerator();
        }

        if (proxyStrategy == null) {
            proxyStrategy = ProxyStrategy.NONE;
        }

        if (proxyStrategy == ProxyStrategy.CUSTOM && proxyPlanner == null) {
            throw new IllegalStateException("proxyPlanner must exist if proxyStrategy is custom");
        }

        CrawlerSessionPool crawlerSessionPool = new CrawlerSessionPool(userManager, loginHandler,
                crawlerHttpClientGenerator, proxyStrategy, ipPool, proxyPlanner);

        if (initSeedSource == null) {
            initSeedSource = new LocalFileSeedSource();
        }

        if (seedKeyResolver == null) {
            seedKeyResolver = new DefaultSeedKeyResolver();
        }

        BerkeleyDBSeedManager berkeleyDBSeedManager = new BerkeleyDBSeedManager(initSeedSource, seedKeyResolver);

        if (processor == null) {
            processor = new PageDownLoadProcessor();
        }

        if (pipelineList.isEmpty()) {
            pipelineList.add(new ConsolePipeline());
        }

        return new VSCrawler(crawlerSessionPool, berkeleyDBSeedManager, processor, pipelineList);
    }
}
