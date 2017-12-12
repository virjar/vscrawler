package com.virjar.vscrawler.core.selector.combine.selectables;

import com.google.common.collect.Lists;
import com.virjar.sipsoup.model.SIPNode;
import com.virjar.sipsoup.model.SipNodes;
import com.virjar.vscrawler.core.selector.combine.AbstractSelectable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;

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

    @Override
    public List<AbstractSelectable<SipNodes>> toMultiSelectable() {
        SipNodes sipNodes = createOrGetModel();
        List<AbstractSelectable<SipNodes>> ret = Lists.newLinkedList();
        for (SIPNode sipNode : sipNodes) {
            XpathNode xpathNode = new XpathNode(getBaseUrl(), sipNode.isText() ? sipNode.getTextVal() : sipNode.getElement().toString());
            xpathNode.setModel(new SipNodes(sipNode));
            ret.add(xpathNode);
        }
        return ret;
    }

    public XpathNode(String rowText) {
        super(rowText);
    }
}
