package com.virjar.vscrawler.core.selector.combine.selectables;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.selector.combine.AbstractSelectable;

/**
 * Created by virjar on 17/6/30.<br/>
 * vs的JSONPath使用 FastJson 作为baseLib
 */
public class JsonNode extends AbstractSelectable<List<JSON>> {
    public JsonNode(String baseUrl, String rowText) {
        super(baseUrl, rowText);
    }

    @Override
    public List<JSON> createOrGetModel() {
        if (model == null) {

            model = Lists.newArrayList((JSON) JSON.toJSON(getRawText()));
        }
        return model;
    }

    public JsonNode(String rowText) {
        super(rowText);
    }
}
