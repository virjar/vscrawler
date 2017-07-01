package com.virjar.vscrawler.core.selector.combine.selectables;

import com.virjar.vscrawler.core.selector.combine.AbstractSelectable;

/**
 * Created by virjar on 17/6/30.
 */
public class RawNode extends AbstractSelectable<String> {
    public RawNode(String baseUrl, String rowText) {
        super(baseUrl, rowText);
    }

    @Override
    public String createOrGetModel() {
        if (model == null) {
            model = getRowText();
        }
        return model;
    }

    public RawNode(String rowText) {
        super(rowText);
    }
}
