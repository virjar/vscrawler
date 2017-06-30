package com.virjar.vscrawler.core.selector.model.selectables;

import java.util.List;

import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.selector.model.AbstractSelectable;

/**
 * Created by virjar on 17/6/30.
 */
public class StringNode extends AbstractSelectable<List<String>> {
    public StringNode(String baseUrl, String rowText) {
        super(baseUrl, rowText);
    }

    public StringNode(String rowText) {
        super(rowText);
    }

    @Override
    public List<String> createOrGetModel() {
        if (model == null) {
            model = Lists.newArrayList(getRowText());
        }
        return model;
    }
}
