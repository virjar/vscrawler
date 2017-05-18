package com.virjar.vscrawler.processor;

import com.virjar.vscrawler.net.session.CrawlerSession;
import com.virjar.vscrawler.seed.Seed;

/**
 * Created by virjar on 17/4/16.
 * 
 * @author virjar
 * @since 0.0.1
 */
public interface IProcessor {
    void process(Seed seed, CrawlerSession crawlerSession, CrawlResult crawlResult);
}
