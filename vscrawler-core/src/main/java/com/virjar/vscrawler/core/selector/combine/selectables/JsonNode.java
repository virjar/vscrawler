package com.virjar.vscrawler.core.selector.combine.selectables;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.selector.combine.AbstractSelectable;
import com.virjar.vscrawler.core.selector.combine.RawTextStringFactory;

import java.util.List;

/**
 * Created by virjar on 17/6/30.<br/>
 * vs的JSONPath使用 FastJson 作为baseLib
 */
public class JsonNode extends AbstractSelectable<List<JSON>> {
    public JsonNode(String baseUrl, String rowText) {
        super(baseUrl, rowText);
    }

    public JsonNode(String baseUrl, RawTextStringFactory rowText) {
        super(baseUrl, rowText);
    }


    @Override
    public List<JSON> createOrGetModel() {
        if (model == null) {
            model = Lists.newArrayList((JSON) JSON.toJSON(getRawText()));
        }
        return model;
    }

    @Override
    public List<AbstractSelectable> toMultiSelectable() {
        List<JSON> models = createOrGetModel();
        List<AbstractSelectable> ret = Lists.newLinkedList();
        for (final JSON json : models) {
            JsonNode jsonNode = new JsonNode(getBaseUrl(), new RawTextStringFactory() {
                @Override
                public String rawText() {
                    return json.toJSONString();
                }
            });
            jsonNode.setModel(Lists.newArrayList(json));
            ret.add(jsonNode);
        }
        return ret;
    }

    public JsonNode(String rowText) {
        super(rowText);
    }
}
