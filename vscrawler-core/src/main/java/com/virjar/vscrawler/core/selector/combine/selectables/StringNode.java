package com.virjar.vscrawler.core.selector.combine.selectables;

import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.selector.combine.AbstractSelectable;

import java.util.List;

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
            model = Lists.newArrayList(getRawText());
        }
        return model;
    }

    @Override
    public List<AbstractSelectable> toMultiSelectable() {
        List<String> models = createOrGetModel();
        List<AbstractSelectable> ret = Lists.newLinkedList();
        for (String string : models) {
            StringNode stringNode = new StringNode(getBaseUrl(), string);
            stringNode.setModel(Lists.newArrayList(string));
            ret.add(stringNode);
        }
        return ret;
    }
}
