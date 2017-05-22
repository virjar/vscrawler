package com.virjar.vscrawler.processor;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.Sets;
import com.virjar.vscrawler.seed.Seed;

/**
 * Created by virjar on 17/4/16.
 * 
 * @author virjar
 * @since 0.0.1
 */
public class PageDownLoadProcessor extends AutoParseSeedProcessor {
    private Set<String> allUrl(Document document) {
        Elements a = document.getElementsByTag("a");
        Set<String> ret = Sets.newHashSet();
        for (Element el : a) {
            ret.add(el.absUrl("href"));
        }
        return ret;
    }

    @Override
    protected void parse(Seed seed, String result, CrawlResult crawlResult) {
        if (StringUtils.isEmpty(result)) {
            return;
        }
        Set<String> strings = allUrl(Jsoup.parse(result, seed.getData()));
        crawlResult.addResults(strings);
        crawlResult.addStrSeeds(strings);
    }
}
