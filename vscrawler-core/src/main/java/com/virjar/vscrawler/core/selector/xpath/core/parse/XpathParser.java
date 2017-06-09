package com.virjar.vscrawler.core.selector.xpath.core.parse;

import com.virjar.vscrawler.core.selector.xpath.model.XpathEvaluator;
import com.virjar.vscrawler.core.selector.xpath.model.XpathTree;

/**
 * Created by virjar on 17/6/9.
 */
public class XpathParser {
    private String xpathStr;
    private TokenQueue tokenQueue;

    public XpathParser(String xpathStr) {
        this.xpathStr = xpathStr;
        tokenQueue = new TokenQueue(xpathStr);
    }

    public XpathEvaluator parse() {
        return null;
    }
}
