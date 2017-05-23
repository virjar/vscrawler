package com.virjar.vscrawler.core.support.webmagic;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.virjar.vscrawler.core.processor.CrawlResult;
import com.virjar.vscrawler.core.support.webmagic.select.JsoupXpathPage;
import com.virjar.vscrawler.core.processor.AutoParseSeedProcessor;
import com.virjar.vscrawler.core.seed.Seed;

import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.PlainText;

/**
 * Created by virjar on 17/5/20.
 */
@Slf4j
public class WebMagicProcessorDelegator extends AutoParseSeedProcessor {
    private PageProcessor pageProcessor;

    public WebMagicProcessorDelegator(PageProcessor pageProcessor) {
        this.pageProcessor = pageProcessor;
    }

    @Override
    protected void parse(Seed seed, String result, CrawlResult crawlResult) {
        if (result == null) {
            seed.retry();
            return;
        }
        JsoupXpathPage jsoupXpathPage = new JsoupXpathPage();
        jsoupXpathPage.setRawText(result);
        jsoupXpathPage.setUrl(new PlainText(seed.getData()));
        jsoupXpathPage.setRequest(CovertUtil.convertSeed(seed));
        jsoupXpathPage.setStatusCode(200);
        pageProcessor.process(jsoupXpathPage);

        // new url
        List<Request> targetRequests = jsoupXpathPage.getTargetRequests();
        for (Request request : targetRequests) {
            crawlResult.addSeed(CovertUtil.covertRequest(request));
        }

        if (!jsoupXpathPage.getResultItems().isSkip()) {
            ResultItems resultItems = jsoupXpathPage.getResultItems();
            crawlResult.addResult(JSONObject.toJSONString(resultItems.getAll()));
        }
    }

}
