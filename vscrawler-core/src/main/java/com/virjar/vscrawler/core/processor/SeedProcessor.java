package com.virjar.vscrawler.core.processor;

import com.virjar.vscrawler.core.net.session.CrawlerSession;
import com.virjar.vscrawler.core.seed.Seed;

/**
 * Created by virjar on 17/4/16.
 * 
 * @author virjar
 * @since 0.0.1
 */
public interface SeedProcessor {
    void process(Seed seed, CrawlerSession crawlerSession, CrawlResult crawlResult);
}
