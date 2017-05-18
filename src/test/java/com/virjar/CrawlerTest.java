package com.virjar;

import com.virjar.vscrawler.VSCrawlerBuilder;

/**
 * Created by virjar on 17/5/18.
 */
public class CrawlerTest {
    public static void main(String[] args) {
        VSCrawlerBuilder.create().build().start();
    }
}
