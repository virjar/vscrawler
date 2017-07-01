package com.virjar.vscrawler.core.selector.combine.selectables;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.virjar.vscrawler.core.selector.combine.AbstractSelectable;

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
            model = Jsoup.parse(getRawText(), getBaseUrl());
        }
        return model;
    }

    public HtmlNode(String rowText) {
        super(rowText);
    }
}
