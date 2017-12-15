package com.virjar.vscrawler.core.selector.combine.selectables;

import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.selector.combine.AbstractSelectable;

import java.util.List;

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
            model = getRawText();
        }
        return model;
    }

    @Override
    public List<AbstractSelectable> toMultiSelectable() {
        return Lists.<AbstractSelectable>newArrayList(this);
    }

    public RawNode(String rowText) {
        super(rowText);
    }
}
