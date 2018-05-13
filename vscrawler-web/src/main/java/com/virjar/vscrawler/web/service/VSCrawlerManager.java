package com.virjar.vscrawler.web.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.virjar.vscrawler.core.VSCrawler;
import com.virjar.vscrawler.core.util.ClassScanner;
import com.virjar.vscrawler.core.util.PathResolver;
import com.virjar.vscrawler.web.api.CrawlerBuilder;
import com.virjar.vscrawler.web.crawlerloader.VSCrawlerClassLoader;
import com.virjar.vscrawler.web.model.CrawlerBean;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;

/**
 * Created by virjar on 2018/1/17.<br>
 */
@Service
@Slf4j
public class VSCrawlerManager implements ApplicationListener<ContextRefreshedEvent> {
    private Map<String, CrawlerBean> allCrawler = Maps.newConcurrentMap();
    private boolean hasInit = false;
    @Getter
    private String webAppPath;

    public Collection<CrawlerBean> getAllCrawler() {
        return allCrawler.values();
    }

    public VSCrawler get(String appSource) {
        return allCrawler.get(appSource).getCrawler();
    }

    private synchronized void init() {
        if (hasInit) {
            return;
        }

        //cannot auto inject by spring framework,if there no implementations ,a exception will be throw
        Map<String, CrawlerBuilder> beansOfType = webApplicationContext.getBeansOfType(CrawlerBuilder.class);
        crawlerBuilderList.addAll(beansOfType.values());

        //load system crawler
        for (CrawlerBuilder crawlerBuilder : crawlerBuilderList) {
            VSCrawler vsCrawler = crawlerBuilder.build();
            allCrawler.put(vsCrawler.getVsCrawlerContext().getCrawlerName(), new CrawlerBean(vsCrawler));
        }

        //load jar file
        //find jar file root dir
        File jarDir = new File(calcHotJarDir());
        moveEmbedCrawler(jarDir);
        loadHotJar(jarDir);
        hasInit = true;
    }

    private void moveEmbedCrawler(File jarDir) {
        ClassLoader classLoader = VSCrawlerManager.class.getClassLoader();

        URL resource = classLoader.getResource("crawlers");
        System.out.println(resource);
        try {
            System.out.println(resource.getContent());
            System.out.println(resource.getContent() instanceof JarFile);
            System.out.println(resource.getContent().getClass().getSuperclass());


            URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
            URL[] urLs = urlClassLoader.getURLs();
            for (URL url : urLs) {
                System.out.println(url.toString());
                System.out.println(url.getContent());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Set<URL> jars = ClassScanner.findJars(classLoader);
//        for (URL url : jars) {
//            System.out.println(url);
//        }
    }

    private void loadHotJar(File dir) {
        if (!dir.exists()) {
            return;
        }
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
            try {
                CrawlerBean crawlerBean = loadJarFile(file);
                if (crawlerBean == null) {
                    continue;
                }
                String crawlerName = crawlerBean.getCrawler().getVsCrawlerContext().getCrawlerName();
                if (allCrawler.containsKey(crawlerName)) {
                    throw new IllegalStateException("duplicate crawler defined :" + crawlerName);
                }
                allCrawler.put(crawlerName, crawlerBean);
            } catch (Exception e) {
                log.error("error when load jar file,this crawler will be ignore", e);
            }
        }
    }

    private CrawlerBean loadJarFile(File jarFile) throws Exception {
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
            return vsCrawlerClassLoader.loadCrawler(subClassVisitor.getSubClass().get(0).getName(), webApplicationContext);
        } finally {
            Thread.currentThread().setContextClassLoader(originContextClassLoader);
        }
    }


    //@Resource
    private List<CrawlerBuilder> crawlerBuilderList = Lists.newArrayList();


    @PreDestroy
    public void destroy() {
        for (CrawlerBean vsCrawler : allCrawler.values()) {
            vsCrawler.getCrawler().stopCrawler();
        }
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
        webApplicationContext = (WebApplicationContext) applicationContext;
        ServletContext servletContext = webApplicationContext.getServletContext();
        webAppPath = servletContext.getRealPath("/");
        init();
    }

    private WebApplicationContext webApplicationContext;

    private String calcHotJarDir() {
        File hotJarDir = new File(PathResolver.resolveAbsolutePath("file:~/.vscrawler/crawlers/"));
        if (!hotJarDir.exists()) {
            if (!hotJarDir.mkdirs()) {
                log.warn("cat not create director for vscrawler hot jar lib ");
            }
        }
        return hotJarDir.getAbsolutePath();
    }

    public void reloadJar(MultipartFile multipartFile) throws Exception {
        String fileName = multipartFile.getName();
        if (StringUtils.isBlank(fileName)) {
            fileName = multipartFile.getOriginalFilename();
        }
        if (StringUtils.isBlank(fileName)) {
            fileName = String.valueOf(System.currentTimeMillis()) + ".jar";
        }
        File hotJarDir = new File(calcHotJarDir());

        //calc a jar file name
        int slashIndex = fileName.lastIndexOf("/");
        if (slashIndex > 0) {
            fileName = fileName.substring(slashIndex);
        }
        slashIndex = fileName.lastIndexOf(".");
        if (slashIndex > 0) {
            fileName = fileName.substring(0, slashIndex);
        }

        // get a file path
        String finalFileName;
        while (true) {
            finalFileName = fileName + "_" + System.currentTimeMillis() + ".jar";
            if (!new File(hotJarDir, finalFileName).exists()) {
                break;
            }
        }
        //save file to file system
        File jarFile = new File(hotJarDir, finalFileName);
        multipartFile.transferTo(jarFile);

        try {
            //scan and load crawler
            CrawlerBean crawlerBean = loadJarFile(jarFile);
            if (crawlerBean == null) {
                throw new IllegalStateException("not crawler defined in this jar file");
            }

            //stop old crawler if necessary
            String crawlerName = crawlerBean.getCrawler().getVsCrawlerContext().getCrawlerName();
            CrawlerBean oldVSCrawler = allCrawler.get(crawlerName);
            if (oldVSCrawler != null) {
                if (!oldVSCrawler.isReloadAble()) {
                    throw new IllegalStateException("can not reload crawler " + crawlerName + " ,this crawler defined in servlet context,not defined in vscrawler context ");
                }
                // 这里可能比较耗时
                oldVSCrawler.getCrawler().stopCrawler();
                deleteJarIfJarIllegal(oldVSCrawler.relatedJarFile());
            }
            //register new crawler
            allCrawler.put(crawlerName, crawlerBean);
        } catch (Exception e) {
            deleteJarIfJarIllegal(jarFile);
            throw e;
        }
    }

    private void deleteJarIfJarIllegal(File jarFilePath) {
        if (!jarFilePath.delete()) {
            log.warn("delete file:{} failed", jarFilePath.getAbsoluteFile());
        }
    }

}
