package com.virjar.vscrawler.core.selector.combine.selectables;

import com.virjar.vscrawler.core.selector.combine.AbstractSelectable;

/**
 * Created by virjar on 17/6/30.
 */
// TODO
public class JsonNode extends AbstractSelectable {
    public JsonNode(String baseUrl, String rowText) {
        super(baseUrl, rowText);
    }

    @Override
    public Object createOrGetModel() {
        return null;
    }

    public JsonNode(String rowText) {
        super(rowText);
    }
}
