package com.virjar.vscrawler.core.selector.combine.selectables;

import com.alibaba.fastjson.JSON;
import com.virjar.vscrawler.core.selector.combine.AbstractSelectable;

/**
 * Created by virjar on 17/6/30.<br/>
 * vs的JSONPath使用 FastJson 作为baseLib
 */
public class JsonNode extends AbstractSelectable<JSON> {
    public JsonNode(String baseUrl, String rowText) {
        super(baseUrl, rowText);
    }

    @Override
    public JSON createOrGetModel() {
        if (model == null) {
            model = (JSON) JSON.toJSON(getRawText());
        }
        return model;
    }

    public JsonNode(String rowText) {
        super(rowText);
    }
}
