package com.virjar.vscrawler.core.support.webmagic;

import java.util.Collection;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.virjar.vscrawler.core.serialize.Pipeline;
import com.virjar.vscrawler.core.seed.Seed;

import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.ResultItems;

/**
 * Created by virjar on 17/5/20.
 */
@Slf4j
public class WebMagicPipelineDelegator implements Pipeline {

    private us.codecraft.webmagic.pipeline.Pipeline webMagicPipeline;

    public WebMagicPipelineDelegator(us.codecraft.webmagic.pipeline.Pipeline webMagicPipeline) {
        this.webMagicPipeline = webMagicPipeline;
    }

    @Override
    public void saveItem(Collection<String> itemJson, Seed seed) {
        for (String str : itemJson) {
            ResultItems resultItems = new ResultItems();
            resultItems.setRequest(CovertUtil.convertSeed(seed));
            try {
                JSONObject jsonObject = JSON.parseObject(str);
                for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                    resultItems.put(entry.getKey(), entry.getValue());
                }
            } catch (Exception e) {
                log.warn("craw result is not a json format:{}", str);
                resultItems.put("data", str);
            }
            try {
                webMagicPipeline.process(resultItems, null);
            } catch (Exception e) {
                log.error("error when process result", e);
            }
        }
    }
}
