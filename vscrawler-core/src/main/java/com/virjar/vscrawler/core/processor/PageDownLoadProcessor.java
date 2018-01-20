package com.virjar.vscrawler.core.processor;

import com.virjar.sipsoup.parse.XpathParser;
import com.virjar.vscrawler.core.seed.Seed;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;

/**
 * Created by virjar on 17/4/16.
 *
 * @author virjar
 * @since 0.0.1
 */
public class PageDownLoadProcessor extends AutoParseSeedProcessor {
    protected List<String> allUrl(Document document) {
        return XpathParser.compileNoError("/css('a')::absUrl('href')").evaluateToString(document);
    }

    @Override
    protected void parse(Seed seed, String result, GrabResult crawlResult) {
        if (StringUtils.isEmpty(result)) {
            return;
        }
        List<String> strings = allUrl(Jsoup.parse(result, seed.getData()));
        crawlResult.addResults(strings);
        crawlResult.addStrSeeds(strings);
    }
}
