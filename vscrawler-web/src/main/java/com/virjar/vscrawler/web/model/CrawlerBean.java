package com.virjar.vscrawler.web.model;

import com.virjar.vscrawler.core.VSCrawler;
import com.virjar.vscrawler.web.crawlerloader.VSCrawlerClassLoader;
import lombok.Getter;

import java.io.File;

/**
 * Created by virjar on 2018/2/1.<br>
 * 包裹一个爬虫对象,包括爬虫相关描述参数
 */
public class CrawlerBean {
    @Getter
    private VSCrawler crawler;

    /**
     * a crawler implement in web application class context,vscrawler framework reject reload grab rule
     * for this crawler,the reason is this crawler java class is not writeable,framework can not load newest
     * class when program restart next time, for jar hot load please upload crawler class to webapp lib directory with jar format file
     */
    @Getter
    private boolean reloadable = false;

    @Getter
    private String codeUrl;

    public CrawlerBean(VSCrawler crawler) {
        this.crawler = crawler;
    }

    public CrawlerBean(VSCrawler crawler, boolean reloadable, VSCrawlerClassLoader vsCrawlerClassLoader) {
        this.crawler = crawler;
        this.reloadable = reloadable;
        this.vsCrawlerClassLoader = vsCrawlerClassLoader;
    }

    @Getter
    private VSCrawlerClassLoader vsCrawlerClassLoader;

    public File relatedJarFile() {
        if (vsCrawlerClassLoader == null) {
            return null;
        }
        return vsCrawlerClassLoader.getJarFile();
    }

    public String crawlerName() {
        return crawler.getVsCrawlerContext().getCrawlerName();
    }
}
