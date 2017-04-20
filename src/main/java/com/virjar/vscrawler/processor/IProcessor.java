package com.virjar.vscrawler.processor;

import com.virjar.vscrawler.net.session.CrawlerSession;

/**
 * Created by virjar on 17/4/16.
 */
public interface IProcessor {
    CrawlResult process(String seed, CrawlerSession crawlerSession);
}
