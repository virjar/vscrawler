package com.virjar.vscrawler.samples.processor.meitustar;

import com.virjar.vscrawler.core.VSCrawlerBuilder;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.AnnotationProcessorBuilder;

/**
 * Created by virjar on 2017/12/16.
 */
public class CrawlerStarter {
    public static void main(String[] args) {
        VSCrawlerBuilder
                .create()
                .setCrawlerName("meitustar")
                .setProcessor(AnnotationProcessorBuilder
                        .create()
                        .addBeanPackage("com.virjar.vscrawler.samples.processor.meitustar.model")
                        .build()
                )
                .build()
                .clearTask()
                .pushSeed("https://www.meitulu.com/t/chise-nakamura/")
                .run();

    }
}
