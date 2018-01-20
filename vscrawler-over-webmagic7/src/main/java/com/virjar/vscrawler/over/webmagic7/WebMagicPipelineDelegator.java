package com.virjar.vscrawler.over.webmagic7;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.virjar.dungproxy.client.util.ReflectUtil;
import com.virjar.vscrawler.core.processor.GrabResult;
import com.virjar.vscrawler.core.seed.Seed;
import com.virjar.vscrawler.core.serialize.Pipeline;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.ResultItems;

import java.lang.reflect.Field;
import java.util.Map;

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
    public void saveItem(GrabResult grabResult, Seed seed) {
        for (Object str : grabResult.allEntityResult()) {
            ResultItems resultItems = new ResultItems();
            resultItems.setRequest(CovertUtil.convertSeed(seed));
            if (str instanceof CharSequence) {
                handleJson(resultItems, str.toString());
            } else {
                handleJsonObject(resultItems, str);
            }
            try {
                webMagicPipeline.process(resultItems, null);
            } catch (Exception e) {
                log.error("error when process result", e);
            }
        }
    }

    private void handleJsonObject(ResultItems resultItems, Object obj) {
        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            try {
                resultItems.put(field.getName(), ReflectUtil.getField(obj, field.getName()));
            } catch (Exception e) {
                //ignore,not happen
            }
        }
    }

    private void handleJson(ResultItems resultItems, String str) {
        try {
            JSONObject jsonObject = JSON.parseObject(str);
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                resultItems.put(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            log.warn("craw result is not a json format:{}", str);
            resultItems.put("data", str);
        }
    }
}
