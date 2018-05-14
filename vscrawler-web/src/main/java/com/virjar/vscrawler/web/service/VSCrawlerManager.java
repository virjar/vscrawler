package com.virjar.vscrawler.web.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.virjar.vscrawler.core.VSCrawler;
import com.virjar.vscrawler.core.util.ClassScanner;
import com.virjar.vscrawler.core.util.PathResolver;
import com.virjar.vscrawler.web.api.CrawlerBuilder;
import com.virjar.vscrawler.web.crawlerloader.VSCrawlerClassLoader;
import com.virjar.vscrawler.web.model.CrawlerBean;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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

    private String getFileSign(InputStream inputStream) {
        byte[] buff = new byte[1024];
        byte[] sign = new byte[32];
        for (int i = 0; i < sign.length; i++) {
            sign[i] = 0;
        }
        try {
            int readSize;
            //不要一个字节一个字节的读,这样会因为堆栈开销影响性能,读取需要批量读,然后再同一个栈帧内循环处理数据
            while ((readSize = inputStream.read(buff)) > 0) {
                for (int i = 0; i < readSize; i++) {
                    sign[i % sign.length] ^= (buff[i] + i);
                }
            }
        } catch (IOException e) {
            log.warn("failed to read jar file", e);
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder(sign.length * 2);
        for (byte by : sign) {
            stringBuilder.append(Integer.toHexString(by));
        }
        return stringBuilder.toString();
    }

    private String getFileSign(File file) {
        if (file == null || !file.isFile()) {
            return "";
        }
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            return getFileSign(fileInputStream);
        } catch (IOException e) {
            log.warn("failed to read jar file", e);
            return "";
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    private void moveEmbedCrawler(File jarDir) {

        ClassLoader classLoader = VSCrawlerManager.class.getClassLoader();
        URL resource = classLoader.getResource("crawlers");
        if (resource == null) {
            log.warn("can not load default crawlers folder");
            return;
        }

        Set<String> existFileSign = Sets.newHashSet();
        Set<String> existFileNames = Sets.newHashSet();
        //load all exits crawler, to avoid duplicate move
        for (File jarFile : jarDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return StringUtils.endsWith(name, ".jar");
            }
        })) {
            existFileSign.add(getFileSign(jarFile));
            existFileNames.add(jarFile.getName());
        }

        //普通文件夹的方式,该方式可能为war包,也可能是springBoot的main函数执行的方式(没有打jar包)
        if (StringUtils.startsWithIgnoreCase(resource.toString(), "file:")) {
            File fromDir = new File(resource.getPath());
            if (!fromDir.isDirectory()) {
                return;
            }
            for (File jarFile : fromDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return StringUtils.endsWith(name, ".jar");
                }
            })) {
                if (existFileSign.contains(getFileSign(jarFile))) {
                    continue;
                }
                String originFileName = jarFile.getName();
                File toFile = judgeCopyTargetFile(originFileName, existFileNames, jarDir);
                try {
                    Files.copy(jarFile, toFile);
                } catch (IOException e) {
                    log.warn("failed to copy file,from :{}  to:{}", jarFile, toFile);
                }
            }
            return;
        }

        if (StringUtils.startsWithIgnoreCase(resource.toString(), "jar:file:")) {
            //the fucking SpringBoot
            String urlPath = resource.toString().substring("jar:file:".length());
            int separatorIndex = urlPath.indexOf("!");
            String containerJarPath = urlPath.substring(0, separatorIndex);
            String entryName = trimSlash(urlPath.substring(separatorIndex).replaceAll("!", ""));

            ZipFile containerJarFile = null;
            try {
                containerJarFile = new ZipFile(containerJarPath);
                Enumeration<? extends ZipEntry> entries = containerJarFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry zipEntry = entries.nextElement();
                    String zipEntryName = trimSlash(zipEntry.getName());

                    if (StringUtils.equals(zipEntryName, entryName)) {
                        continue;
                    }
                    if (!StringUtils.startsWith(zipEntryName, entryName)) {
                        continue;
                    }
                    if (!StringUtils.endsWith(zipEntryName, ".jar")) {
                        continue;
                    }
                    InputStream inputStream = containerJarFile.getInputStream(zipEntry);
                    String fileSign = getFileSign(inputStream);
                    IOUtils.closeQuietly(inputStream);
                    if (existFileSign.contains(fileSign)) {
                        continue;
                    }
                    String originFileName = PathResolver.getFileName(zipEntryName);
                    File toFile = judgeCopyTargetFile(originFileName, existFileNames, jarDir);
                    FileOutputStream fileOutputStream = null;
                    try {
                        fileOutputStream = new FileOutputStream(toFile);
                        inputStream = containerJarFile.getInputStream(zipEntry);
                        IOUtils.copy(inputStream, fileOutputStream);
                    } catch (IOException e) {
                        log.warn("failed to copy file,from :{}  to:{}", zipEntry.getName(), toFile);
                    } finally {
                        IOUtils.closeQuietly(inputStream);
                        IOUtils.closeQuietly(fileOutputStream);
                    }
                }

            } catch (IOException e) {
                log.warn("failed to load embed crawler:{}", urlPath, e);
            } finally {
                IOUtils.closeQuietly(containerJarFile);
            }
            return;
        }
        log.warn("can not locate embed crawler :{}", resource.toString());
    }

    private String trimSlash(String path) {
        if (StringUtils.startsWith(path, "/")) {
            path = path.substring(1);
        }
        if (StringUtils.endsWith(path, "/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    private File judgeCopyTargetFile(String originFileName, Set<String> existFileNames, File jarDir) {
        File toFile = new File(jarDir, originFileName);
        if (!existFileNames.contains(originFileName)) {
            return toFile;
        }
        int i = 0;
        while (true) {
            if (!existFileNames.contains(i + originFileName)) {
                return new File(jarDir, i + originFileName);
            }
        }
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
        File hotJarDir = new File(PathResolver.resolveAbsolutePath("file:~/.vscrawler/jar_crawlers/"));
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
        Set<String> existFileSign = Sets.newHashSet();
        Set<String> existFileNames = Sets.newHashSet();
        //load all exits crawler, to avoid duplicate move
        for (File jarFile : hotJarDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return StringUtils.endsWith(name, ".jar");
            }
        })) {
            existFileSign.add(getFileSign(jarFile));
            existFileNames.add(jarFile.getName());
        }
        fileName = PathResolver.getFileName(fileName);
        File targetFile = judgeCopyTargetFile(fileName, existFileNames, hotJarDir);
        multipartFile.transferTo(targetFile);
        if (existFileSign.contains(getFileSign(targetFile))) {
            deleteJarIfJarIllegal(targetFile);
            return;
        }

        try {
            //scan and load crawler
            CrawlerBean crawlerBean = loadJarFile(targetFile);
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
            deleteJarIfJarIllegal(targetFile);
            throw e;
        }
    }

    private void deleteJarIfJarIllegal(File jarFilePath) {
        if (!jarFilePath.delete()) {
            log.warn("delete file:{} failed", jarFilePath.getAbsoluteFile());
        }
    }

}
