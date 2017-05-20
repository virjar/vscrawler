package com.virjar.vscrawler.support.webmagic;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.virjar.vscrawler.seed.Seed;

import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;

/**
 * Created by virjar on 17/5/20.
 */
@Slf4j
public class CovertUtil {
    public static Seed covertRequest(Request request) {
        if (StringUtils.isNotEmpty(request.getMethod()) && !StringUtils.equalsIgnoreCase(request.getMethod(), "get")) {
            log.warn("vscrawler can not support webmagic get method,this request {} will be ignore", request.getUrl());
            return null;
        }
        Seed seed = new Seed(request.getUrl());
        seed.setExt(Maps.transformEntries(request.getExtras(), new Maps.EntryTransformer<String, Object, String>() {
            @Override
            public String transformEntry(String key, Object value) {
                if (value instanceof String) {
                    return (String) value;
                }
                return JSONObject.toJSONString(value);
            }
        }));

        return seed;
    }

    public static Request convertSeed(Seed seed) {
        Request request = new Request(seed.getData());
        request.setMethod("GET");
        Map<String, Object> ext = Maps.newHashMap();
        ext.putAll(seed.getExt());
        request.setExtras(ext);
        return request;
    }

    public static Task task = new Task() {
        private String uuid = UUID.randomUUID().toString();
        private Site site = Site.me();

        @Override
        public String getUUID() {
            return uuid;
        }

        @Override
        public Site getSite() {
            return site;
        }
    };
}
