package com.virjar.vscrawler.web.crawlerloader;

import com.virjar.vscrawler.core.VSCrawler;
import com.virjar.vscrawler.web.crawler.CrawlerBuilder;
import com.virjar.vscrawler.web.model.CrawlerBean;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by virjar on 2018/2/3.<br>
 * 热加载爬虫代码
 */
public class VSCrawlerClassLoader extends URLClassLoader {
    private File jarFile;
    //a class
    private String crawlerEntryName;

    public VSCrawlerClassLoader(File jarFile, ClassLoader parent) throws MalformedURLException {
        super(new URL[]{jarFile.toURI().toURL()}, parent);
        this.jarFile = jarFile;
    }

    /**
     * @param crawlerEntryName 爬虫入口类,应该是com.virjar.vscrawler.web.crawler.CrawlerBuilder的实现类
     * @return 由入口类构造的一个爬虫对象
     * @see CrawlerBuilder
     */
    public CrawlerBean loadCrawler(String crawlerEntryName) throws InstantiationException, IllegalAccessException {
        // check
        try {
            CrawlerBuilder crawlerBuilder = (CrawlerBuilder) loadClass(crawlerEntryName).newInstance();
            VSCrawler vsCrawler = crawlerBuilder.build();
            return new CrawlerBean(vsCrawler, true, this);
        } catch (ClassNotFoundException e) {
            //this exception will not happen
        }
        return null;
    }
}
