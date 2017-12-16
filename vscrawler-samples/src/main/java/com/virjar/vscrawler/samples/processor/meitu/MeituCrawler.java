package com.virjar.vscrawler.samples.processor.meitu;

import com.alibaba.fastjson.annotation.JSONField;
import com.virjar.vscrawler.core.VSCrawler;
import com.virjar.vscrawler.core.VSCrawlerBuilder;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.AbstractAutoProcessModel;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.AnnotationProcessorFactory;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.annotation.AutoProcessor;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.annotation.NewSeed;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.annotation.Xpath;
import lombok.Getter;

import java.util.List;

/**
 * Created by virjar on 2017/12/16.<br/>
 * 之前那个美图网的美女爬虫demo,现在用注解方式实现
 */
@AutoProcessor(seedPattern = "https://www\\.meitulu\\.com/item/[\\d_]+\\.html")
public class MeituCrawler extends AbstractAutoProcessModel {

    @Xpath("/css('.content')::center/img/@src")
    @NewSeed
    @Getter//需要要被fastjson序列化,需要有getter方法
    private List<String> imageUrl;

    @Xpath("/css('#pages a')::self()[contains(text(),'下一页')]/absUrl('href')")
    @NewSeed
    @JSONField(serialize = false)//vsCrawler使用fastJson实现序列化,遵循fastJson序列化规则
    @Getter
    private String nextUrl;

    public static void main(String[] args) {

        VSCrawler vsCrawler = VSCrawlerBuilder.create().setWorkerThreadNumber(10).setCrawlerName("beautyCrawler_Annotation")
                .setProcessor(AnnotationProcessorFactory.create().addBeanPackage("com.virjar.vscrawler.samples.processor.meitu").build())
                .build();
        vsCrawler.start();
        vsCrawler.clearTask();
        vsCrawler.pushSeed("https://www.meitulu.com/item/2125.html");
        vsCrawler.pushSeed("https://www.meitulu.com/item/6892.html");
        vsCrawler.pushSeed("https://www.meitulu.com/item/2124.html");
        vsCrawler.pushSeed("https://www.meitulu.com/item/2120.html");
        vsCrawler.pushSeed("https://www.meitulu.com/item/2086.html");
    }
}
