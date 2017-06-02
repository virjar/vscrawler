package com.virjar.vscrawler.over.webmagic7.select;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;

/**
 * Created by virjar on 17/5/19.
 */
public class JsoupXpathPage extends Page {
    private JsoupXpathHtml jsoupXpathHtml = null;

    @Override
    public Html getHtml() {
        if (jsoupXpathHtml == null) {
            jsoupXpathHtml = new JsoupXpathHtml(super.getHtml().getDocument());
        }
        return jsoupXpathHtml;
    }
}
