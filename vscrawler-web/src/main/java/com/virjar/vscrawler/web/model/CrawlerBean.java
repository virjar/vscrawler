package com.virjar.vscrawler.web.model;

import com.virjar.vscrawler.core.VSCrawler;
import com.virjar.vscrawler.web.crawlerloader.VSCrawlerClassLoader;
import lombok.Getter;

/**
 * Created by virjar on 2018/2/1.
 */
public class CrawlerBean {
    @Getter
    private VSCrawler crawler;

    /**
     * a crawler implement in web application class context,vscrawler framework reject reload grab rule
     * for this crawler,the reason is this crawler java class is not write able,framework can not load newest
     * class when program restart next time, for jar hot load please upload crawler class to webapp lib directory with jar format file
     */
    @Getter
    private boolean reloadAble;

    public CrawlerBean(VSCrawler crawler) {
        this.crawler = crawler;
    }

    private VSCrawlerClassLoader vsCrawlerClassLoader;
}
