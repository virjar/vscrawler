package com.virjar;

import com.virjar.dungproxy.client.util.CommonUtil;
import com.virjar.vscrawler.core.VSCrawler;
import com.virjar.vscrawler.core.VSCrawlerBuilder;

/**
 * Created by virjar on 17/5/18.
 */
public class CrawlerTest {
    public static void main(String[] args) {

        // 启动爬虫
        VSCrawler vsCrawler = VSCrawlerBuilder.create().addPipeline(new EmptyPipeline()).build();
        vsCrawler.start();

        System.out.println("休眠10s,观察爬虫阻塞等待逻辑是否正确");
        // 休眠10s
        CommonUtil.sleep(10000);

        // 增加种子
        System.out.println("注入一个种子任务");
        vsCrawler.pushSeed("http://www.java1234.com/");
    }
}
