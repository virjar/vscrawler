package com.virjar.vscrawler.core.selector.xpath.core.function.axis;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by virjar on 17/6/6.
 * 
 * @author virjar
 * @since 0.0.1 直接子节点
 */
public class ChildFunction implements AxisFunction {
    @Override
    public Elements call(Element e) {
        return e.children();
    }

    @Override
    public String getName() {
        return "child";
    }
}
