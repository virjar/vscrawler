package com.virjar.vscrawler.processor;

import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.Sets;
import com.virjar.vscrawler.net.session.CrawlerSession;
import com.virjar.vscrawler.seed.Seed;

/**
 * Created by virjar on 17/4/16.
 * 
 * @author virjar
 * @since 0.0.1
 */
public class HtmlDownLoadProcessor implements IProcessor {
    private Set<String> allUrl(Document document) {
        Elements a = document.getElementsByTag("a");
        Set<String> ret = Sets.newHashSet();
        for (Element el : a) {
            ret.add(el.absUrl("href"));
        }
        return ret;
    }

    @Override
    public void process(Seed seed, CrawlerSession crawlerSession, CrawlResult crawlResult) {
        String s = crawlerSession.getCrawlerHttpClient().get(seed.getData());
        if (s == null) {
            seed.retry();
            return;
        }
        crawlResult.addResults(allUrl(Jsoup.parse(s, seed.getData())));
    }
}
