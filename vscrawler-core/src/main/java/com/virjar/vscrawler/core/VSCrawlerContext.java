package com.virjar.vscrawler.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.virjar.vscrawler.core.config.VSCrawlerConfigFileWatcher;
import com.virjar.vscrawler.core.event.EventLoop;
import com.virjar.vscrawler.core.event.support.AutoEventRegistry;
import com.virjar.vscrawler.core.resourcemanager.ResourceManager;
import com.virjar.vscrawler.core.resourcemanager.model.ResourceSetting;
import com.virjar.vscrawler.core.resourcemanager.storage.ScoredQueueStore;
import com.virjar.vscrawler.core.util.PathResolver;
import com.virjar.vscrawler.core.util.VSCrawlerConstant;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by virjar on 17/5/4.<br/>
 * context,存放上下文信息,多组件数据共享。这个在0.2.x上面实现,他的实现代表多站点爬虫开始实现
 *
 * @author virjar
 * @since 0.2.0
 */
@Slf4j
@RequiredArgsConstructor
public class VSCrawlerContext {

    @Getter
    @NonNull
    private String crawlerName;

    @Getter
    private String workPath;

    @Getter
    @NonNull
    private EventLoop eventLoop;

    @Getter
    @Setter
    private AutoEventRegistry autoEventRegistry;

    @Getter
    @Setter
    private ResourceManager resourceManager;

    @Getter
    @Setter
    private ScoredQueueStore scoredQueueStore;

    @Getter
    @Setter
    private ResourceSetting resourceSetting;

    // 爬虫主控文件监听器
    public static VSCrawlerConfigFileWatcher vsCrawlerConfigFileWatcher = new VSCrawlerConfigFileWatcher();

    private static ConcurrentMap<String, VSCrawlerContext> allContext = Maps.newConcurrentMap();

    public static Collection<VSCrawlerContext> getAllContext() {
        return Lists.newArrayList(allContext.values());
    }

    public static void removeContext(VSCrawlerContext vsCrawlerContext) {
        allContext.remove(vsCrawlerContext.getCrawlerName());
    }

    private void resolveWorkPath() {
        Properties properties = vsCrawlerConfigFileWatcher.loadedProperties();
        workPath = properties.getProperty(VSCrawlerConstant.VSCRAWLER_WORKING_DIRECTORY, "file:~/.vscrawler/work");

        log.info("vsCrawler配置工作目录:{}", workPath);
        workPath = PathResolver.resolveAbsolutePath(workPath);
        workPath = new File(workPath, crawlerName).getAbsolutePath();
        log.info("vsCrawler实际工作目录:{}", workPath);
        File workFile = new File(workPath);
        if (workFile.exists() && !workFile.isDirectory()) {
            throw new IllegalStateException(workPath + "不是目录,无法写入数据");
        }

        if (!workFile.exists()) {
            if (!workFile.mkdirs()) {
                throw new IllegalStateException(workPath + "文件夹创建失败");
            }
        }
    }

    public static VSCrawlerContext create(String crawlerName) {
        if (allContext.containsKey(crawlerName)) {
            return allContext.get(crawlerName);
        }
        synchronized (VSCrawlerContext.class) {
            if (allContext.containsKey(crawlerName)) {
                return allContext.get(crawlerName);
            }

            VSCrawlerContext vsCrawlerContext = new VSCrawlerContext(crawlerName, new EventLoop());
            AutoEventRegistry autoEventRegistry = new AutoEventRegistry(vsCrawlerContext);
            vsCrawlerContext.setAutoEventRegistry(autoEventRegistry);
            vsCrawlerContext.resolveWorkPath();
            vsCrawlerContext.getAutoEventRegistry().registerObserver(vsCrawlerConfigFileWatcher);
            allContext.put(crawlerName, vsCrawlerContext);
            return vsCrawlerContext;
        }
    }

    public String makeUserResourceTag() {
        return getCrawlerName() + "_userManagerAccountKey";
    }


}
