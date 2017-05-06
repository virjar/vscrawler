package com.virjar.vscrawler.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.io.IOUtils;

import com.virjar.vscrawler.event.support.AutoEventRegistry;
import com.virjar.vscrawler.event.systemevent.CrawlerConfigChangeEvent;
import com.virjar.vscrawler.event.systemevent.CrawlerStartEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by virjar on 17/5/2.<br/>
 * 监控vsCrawler.properties的配置变更,实现策略实时变更的功能
 * 
 * @author virjar
 * @since 0.0.1
 */
@Slf4j
public class VSCrawlerConfigFileWatcher implements CrawlerStartEvent {
    private static final String configFileName = "default_vsCrawler.properties";

    private Properties oldProperties = null;
    private AtomicBoolean hasStartWatch = new AtomicBoolean(false);

    public VSCrawlerConfigFileWatcher() {
        AutoEventRegistry.getInstance().registerObserver(this);
    }

    public Properties loadedProperties() {
        watchAndBindEvent();
        if (oldProperties == null) {
            throw new IllegalStateException("不能加载配置,加载发生过异常,请排查后重新启动程序");
        }
        return oldProperties;
    }

    public void watchAndBindEvent() {

        /*
         * AutoEventRegistry.getInstance().registerObserver(new CrawlerConfigChangeEvent() {
         * @Override public void configChange(Properties oldProperties, Properties oldProperties) { } });
         */
        if (hasStartWatch.compareAndSet(false, true)) {
            URL resource = Thread.currentThread().getContextClassLoader().getResource(configFileName);
            String dir = null;
            if (resource == null) {
                dir = Thread.currentThread().getContextClassLoader().getResource("/").getFile();
            } else {
                dir = new File(resource.getFile()).getParent();
            }
            final String file = new File(dir, configFileName).getAbsolutePath();

            loadFileAndSendEvent(file);

            DirectoryWatcher.WatcherCallback watcherCallback = new DirectoryWatcher.WatcherCallback() {
                private long lastExecute = System.currentTimeMillis();

                @Override
                public void execute(WatchEvent.Kind<?> kind, String path) {
                    if (System.currentTimeMillis() - lastExecute > 1000) {
                        lastExecute = System.currentTimeMillis();
                        if (!path.equals(file)) {
                            return;
                        }
                        loadFileAndSendEvent(file);
                    }
                }

            };
            DirectoryWatcher fileWatcher = DirectoryWatcher.getDirectoryWatcher(watcherCallback,
                    StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
            fileWatcher.watchDirectory(dir);
        }
    }

    private void loadFileAndSendEvent(String filePath) {
        Properties tempProperties = new Properties();
        FileInputStream fileInputStream = null;
        InputStream defaultConfigInput = null;
        try {
            defaultConfigInput = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("/default_vsCrawler.properties");
            tempProperties.load(defaultConfigInput);// 先加载默认配置

            fileInputStream = new FileInputStream(new File(filePath));
            tempProperties.load(fileInputStream);// 然后使用用户热发配置覆盖

            // 没有报异常才发送通知
            AutoEventRegistry.getInstance().findEventDeclaring(CrawlerConfigChangeEvent.class)
                    .configChange(oldProperties, tempProperties);
            oldProperties = tempProperties;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(defaultConfigInput);
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    @Override
    public void onCrawlerStart() {
        watchAndBindEvent();
        if (oldProperties == null) {
            throw new IllegalStateException("不能加载配置,加载发生过异常,请排查后重新启动程序");
        }
        AutoEventRegistry.getInstance().findEventDeclaring(CrawlerConfigChangeEvent.class).configChange(null,
                oldProperties);
    }
}
