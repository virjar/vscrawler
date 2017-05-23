package com.virjar.vscrawler.core.support.webmagic.select;

import org.jsoup.nodes.Document;

import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

/**
 * Created by virjar on 17/5/20.
 */
public class JsoupXpathHtml extends Html {
    public JsoupXpathHtml(Document document) {
        super(document);
    }

    public JsoupXpathHtml(String text) {
        super(text);
    }

    @Override
    public Selectable xpath(String xpath) {
        return selectElements(new XpathSelector(xpath));
    }
}
