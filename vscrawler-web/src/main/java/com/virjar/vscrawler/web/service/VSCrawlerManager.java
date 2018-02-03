package com.virjar.vscrawler.web.service;

import com.google.common.collect.Maps;
import com.virjar.vscrawler.core.VSCrawler;
import com.virjar.vscrawler.core.util.ClassScanner;
import com.virjar.vscrawler.web.crawler.CrawlerBuilder;
import com.virjar.vscrawler.web.crawlerloader.VSCrawlerClassLoader;
import com.virjar.vscrawler.web.model.CrawlerBean;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

/**
 * Created by virjar on 2018/1/17.<br>
 */
@Service
@Slf4j
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

        //load system crawler
        for (CrawlerBuilder crawlerBuilder : crawlerBuilderList) {
            VSCrawler vsCrawler = crawlerBuilder.build();
            allCrawler.put(vsCrawler.getVsCrawlerContext().getCrawlerName(), new CrawlerBean(vsCrawler));
        }

        //load jar file
        //find jar file root dir
        File dir = new File(webappPath, "WEB-INF/vscrawler_hot_jar");
        if (dir.exists() && dir.isDirectory()) {
            loadHostJar(dir);
        }
        hasInit = true;
    }

    private void loadHostJar(File dir) {
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return StringUtils.endsWith(name, ".jar");
            }
        });
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                continue;
            }
            CrawlerBean crawlerBean = loadJarFile(file);
            if (crawlerBean == null) {
                continue;
            }
            allCrawler.put(crawlerBean.getCrawler().getVsCrawlerContext().getCrawlerName(), crawlerBean);
        }
    }

    private CrawlerBean loadJarFile(File jarFile) {
        ClassLoader originContextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            VSCrawlerClassLoader vsCrawlerClassLoader = new VSCrawlerClassLoader(jarFile, originContextClassLoader);
            Thread.currentThread().setContextClassLoader(vsCrawlerClassLoader);
            ClassScanner.SubClassVisitor<CrawlerBuilder> subClassVisitor = new ClassScanner.SubClassVisitor<>(true, CrawlerBuilder.class);
            //prevent scan parent class loader
            ClassScanner.scanJarFile(new JarFile(jarFile), subClassVisitor);
            if (subClassVisitor.getSubClass().size() == 0) {
                return null;
            }
            if (subClassVisitor.getSubClass().size() != 1) {
                log.error("a crawler jar can only create one crawler,but find {} in {},{}, this jar file load while be ignore"
                        , subClassVisitor.getSubClass().size(), jarFile.getAbsoluteFile()
                        , StringUtils.join(subClassVisitor.getSubClass(), ","));
                return null;
            }
            return vsCrawlerClassLoader.loadCrawler(subClassVisitor.getSubClass().get(0).getName());
        } catch (Exception e) {
            log.error("error when load hot crawler", e);
        } finally {
            Thread.currentThread().setContextClassLoader(originContextClassLoader);
        }
        return null;
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
