package com.virjar.vscrawler.web.service;

import com.google.common.collect.Maps;
import com.virjar.vscrawler.core.VSCrawler;
import com.virjar.vscrawler.web.crawler.CrawlerBuilder;
import com.virjar.vscrawler.web.model.CrawlerBean;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.util.List;
import java.util.Map;

/**
 * Created by virjar on 2018/1/17.<br>
 */
@Service
public class VSCrawlerManager implements ApplicationListener<ContextRefreshedEvent> {
    private Map<String, CrawlerBean> allCrawler = Maps.newConcurrentMap();
    private boolean hasInit = false;
    private String webappPath;

    public VSCrawler get(String appSource) {
        if (!hasInit) {
            init();
        }
        return allCrawler.get(appSource).getCrawler();
    }

    private synchronized void init() {
        if (hasInit) {
            return;
        }
        for (CrawlerBuilder crawlerBuilder : crawlerBuilderList) {
            VSCrawler vsCrawler = crawlerBuilder.build();
            allCrawler.put(vsCrawler.getVsCrawlerContext().getCrawlerName(), new CrawlerBean(vsCrawler));
        }
        hasInit = true;
    }

    @Resource
    @Setter
    private List<CrawlerBuilder> crawlerBuilderList;


    @PreDestroy
    public void destroy() {
        for (CrawlerBean vsCrawler : allCrawler.values()) {
            vsCrawler.getCrawler().stopCrawler();
        }
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
        WebApplicationContext webApplicationContext = (WebApplicationContext) applicationContext;
        ServletContext servletContext = webApplicationContext.getServletContext();
        webappPath = servletContext.getRealPath("/");
    }
}
