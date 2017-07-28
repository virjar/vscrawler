package com.virjar.vscrawler.core.selector.combine.selectables;

import com.virjar.sipsoup.model.SIPNode;
import com.virjar.sipsoup.model.SipNodes;
import com.virjar.vscrawler.core.selector.combine.AbstractSelectable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by virjar on 17/6/30.
 */
public class XpathNode extends AbstractSelectable<SipNodes> {
    public XpathNode(String baseUrl, String rowText) {
        super(baseUrl, rowText);
    }

    @Override
    public SipNodes createOrGetModel() {
        if (model == null) {
            try {
                Document document = Jsoup.parse(getRawText(), getBaseUrl());
                if (document == null) {
                    throw new RuntimeException();
                }
                model = new SipNodes(SIPNode.e(document));
            } catch (Exception e) {
                model = new SipNodes(SIPNode.t(getRawText()));
            }
        }
        return model;
    }

    public XpathNode(String rowText) {
        super(rowText);
    }
}
