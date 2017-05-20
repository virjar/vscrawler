package com.virjar;

import com.virjar.dungproxy.client.util.CommonUtil;
import com.virjar.vscrawler.VSCrawler;
import com.virjar.vscrawler.VSCrawlerBuilder;

/**
 * Created by virjar on 17/5/18.
 */
public class CrawlerTest {
    public static void main(String[] args) {

        // 启动爬虫
        VSCrawler vsCrawler = VSCrawlerBuilder.create().build();
        vsCrawler.start();

        // 休眠10s
        CommonUtil.sleep(10000);

        // 增加种子
        vsCrawler.pushSeed("http://www.java1234.com/");
    }
}
