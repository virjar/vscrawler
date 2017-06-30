package com.virjar.vscrawler.core.selector.model.selectables;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.virjar.vscrawler.core.selector.model.AbstractSelectable;

/**
 * Created by virjar on 17/6/30.
 */
public class HtmlNode extends AbstractSelectable<Element> {
    public HtmlNode(String baseUrl, String rowText) {
        super(baseUrl, rowText);
    }

    @Override
    public Element createOrGetModel() {
        if (model == null) {
            model = Jsoup.parse(getRowText(), getBaseUrl());
        }
        return model;
    }

    public HtmlNode(String rowText) {
        super(rowText);
    }
}
