package com.virjar.vscrawler.over.webmagic7;

import com.alibaba.fastjson.JSONObject;
import com.virjar.vscrawler.core.processor.AutoParseSeedProcessor;
import com.virjar.vscrawler.core.processor.GrabResult;
import com.virjar.vscrawler.core.seed.Seed;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.PlainText;

import java.util.List;

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
    protected void parse(Seed seed, String result, GrabResult crawlResult) {
        if (result == null) {
            seed.retry();
            return;
        }
        SipSoupPage sipSoupPage = new SipSoupPage();
        sipSoupPage.setRawText(result);
        sipSoupPage.setUrl(new PlainText(seed.getData()));
        sipSoupPage.setRequest(CovertUtil.convertSeed(seed));
        sipSoupPage.setStatusCode(200);
        pageProcessor.process(sipSoupPage);

        // new url
        List<Request> targetRequests = sipSoupPage.getTargetRequests();
        for (Request request : targetRequests) {
            crawlResult.addSeed(CovertUtil.covertRequest(request));
        }

        if (!sipSoupPage.getResultItems().isSkip()) {
            ResultItems resultItems = sipSoupPage.getResultItems();
            crawlResult.addResult(JSONObject.toJSONString(resultItems.getAll()));
        }
    }

}
