package com.virjar.vscrawler.core.selector.model.selectables;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import com.virjar.vscrawler.core.selector.model.AbstractSelectable;

/**
 * Created by virjar on 17/6/30.
 */
public class ElementsNode extends AbstractSelectable<Elements> {
    public ElementsNode(String baseUrl, String rowText) {
        super(baseUrl, rowText);
    }

    public ElementsNode(String rowText) {
        super(rowText);
    }

    @Override
    public Elements createOrGetModel() {
        if (model == null) {
            model = new Elements(Jsoup.parse(getBaseUrl(), getRowText()));
        }
        return model;
    }
}
