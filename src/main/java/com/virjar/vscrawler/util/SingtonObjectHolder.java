package com.virjar.vscrawler.util;

import com.virjar.vscrawler.config.VSCrawlerConfigFileWatcher;

/**
 * Created by virjar on 17/5/2.
 */
public class SingtonObjectHolder {

    // 爬虫主控文件监听器
    public static final VSCrawlerConfigFileWatcher vsCrawlerConfigFileWatcher = new VSCrawlerConfigFileWatcher();
}
