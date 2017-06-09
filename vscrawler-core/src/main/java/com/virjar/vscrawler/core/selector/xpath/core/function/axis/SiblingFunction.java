package com.virjar.vscrawler.core.selector.xpath.core.function.axis;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by virjar on 17/6/6.
 * 
 * @author virjar
 * @since 0.0.1 全部同胞（扩展）
 */
public class SiblingFunction implements AxisFunction {
    @Override
    public Elements call(Element e, String... args) {
        return e.siblingElements();
    }

    @Override
    public String getName() {
        return "sibling";
    }
}
