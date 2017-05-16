package com.virjar.vscrawler.util;

import com.virjar.vscrawler.config.VSCrawlerConfigFileWatcher;

/**
 * Created by virjar on 17/5/2.
 * @author virjar
 * @since 0.0.1
 */
public class SingtonObjectHolder {

    // 爬虫主控文件监听器
    public static final VSCrawlerConfigFileWatcher vsCrawlerConfigFileWatcher = new VSCrawlerConfigFileWatcher();

    public static String workPath = null;
}
