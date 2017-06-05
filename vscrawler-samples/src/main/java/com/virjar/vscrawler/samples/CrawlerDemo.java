package com.virjar.vscrawler.samples;

import com.virjar.dungproxy.client.util.CommonUtil;
import com.virjar.vscrawler.core.VSCrawler;
import com.virjar.vscrawler.core.VSCrawlerBuilder;

/**
 * Created by virjar on 17/5/18.
 */
public class CrawlerDemo {
    public static void main(String[] args) {

        // 启动爬虫
        VSCrawler vsCrawler = VSCrawlerBuilder.create()// 创建一个构造器
                .addPipeline(new EmptyPipeline())// 添加一个空的pipeline,为了测试
                .setSessionPoolCoreSize(15)// 核心15个用户
                .setSessionPoolMaxSize(25)// 最多25个用户
                .setSessionPoolReuseDuration(2 * 60 * 1000)// 每个用户2分钟之内不能同时使用
                .setSessionPoolMaxOnlineDuration(10 * 60 * 1000)// 每个用户最多存活10分钟
                .build();
        vsCrawler.start();

        //System.out.println("休眠10s,观察爬虫阻塞等待逻辑是否正确");
        // 休眠10s
        //CommonUtil.sleep(10000);

        // 增加种子
        System.out.println("注入一个种子任务");
        vsCrawler.pushSeed("http://www.java1234.com/");
    }
}
