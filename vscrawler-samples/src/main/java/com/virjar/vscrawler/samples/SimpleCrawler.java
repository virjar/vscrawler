package com.virjar.vscrawler.samples;

import com.virjar.vscrawler.core.VSCrawler;
import com.virjar.vscrawler.core.VSCrawlerBuilder;

/**
 * Created by virjar on 17/5/18.<br/>
 * 最简单的爬虫
 */
public class SimpleCrawler {
    public static void main(String[] args) {

        // 启动爬虫
        VSCrawler vsCrawler = VSCrawlerBuilder.create()// 创建一个构造器
                .build();

        vsCrawler.start();

        // 增加种子
        System.out.println("注入一个种子任务");
        vsCrawler.pushSeed("http://www.java1234.com/");
    }
}
