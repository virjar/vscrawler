package com.virjar;

import java.util.List;

import com.virjar.vscrawler.core.selector.xpath.core.XpathEvaluator;
import com.virjar.vscrawler.core.selector.xpath.model.Node;

/**
 * Created by virjar on 17/6/5.
 */
public class XpathTest {
    public static void main(String[] args) {
        XpathEvaluator xpathEvaluator = new XpathEvaluator();
        List<Node> xpathNodeTree = xpathEvaluator.getXpathNodeTree("//meta[@charset]");

        xpathEvaluator.getXpathNodeTree("bookstore//book");
    }
}
