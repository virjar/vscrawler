package com.virjar.vscrawler.web.sample;

import com.virjar.vscrawler.core.VSCrawler;
import com.virjar.vscrawler.web.api.CrawlerBuilder;

/**
 * Created by virjar on 2018/2/3.<br>
 * 这是一个简单的爬虫类,她可以使用maven打包成为一个jar包,然后上传到vscrawler web 平台,vscrawler-web将会对该jar包实现热加载
 */
public class SampleHotCrawler implements CrawlerBuilder {
    @Override
    public VSCrawler build() {

        return null;
    }
}
