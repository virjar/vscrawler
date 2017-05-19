package com.virjar.vscrawler.processor;

import java.net.MalformedURLException;
import java.net.URL;

import com.virjar.vscrawler.net.session.CrawlerSession;
import com.virjar.vscrawler.seed.Seed;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by virjar on 17/5/20.
 */
@Slf4j
public abstract class AutoParseSeedProcessor implements SeedProcessor {
    @Override
    public void process(Seed seed, CrawlerSession crawlerSession, CrawlResult crawlResult) {
        URL url;
        try {
            url = new URL(seed.getData());
        } catch (MalformedURLException e) {
            log.warn("this seed is not a url:{}", seed.getData(), e);
            seed.setIgnore(true);
            return;
        }
        parse(seed, download(crawlerSession, url), crawlResult);
    }

    abstract void parse(Seed seed, String result, CrawlResult crawlResult);

    abstract String download(CrawlerSession crawlerSession, URL url);
}
