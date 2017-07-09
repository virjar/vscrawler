package com.virjar.vscrawler.core.selector.combine.selectables;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.common.collect.Lists;
import com.virjar.sipsoup.model.SIPNode;
import com.virjar.vscrawler.core.selector.combine.AbstractSelectable;

/**
 * Created by virjar on 17/6/30.
 */
public class XpathNode extends AbstractSelectable<List<SIPNode>> {
    public XpathNode(String baseUrl, String rowText) {
        super(baseUrl, rowText);
    }

    @Override
    public List<SIPNode> createOrGetModel() {
        if (model == null) {
            try {
                Document document = Jsoup.parse(getRawText(), getBaseUrl());
                if (document == null) {
                    throw new RuntimeException();
                }
                model = Lists.newArrayList(SIPNode.e(document));
            } catch (Exception e) {
                model = Lists.newArrayList(SIPNode.t(getRawText()));
            }
        }
        return model;
    }

    public XpathNode(String rowText) {
        super(rowText);
    }
}
