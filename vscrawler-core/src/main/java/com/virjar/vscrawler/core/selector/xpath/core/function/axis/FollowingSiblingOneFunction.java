package com.virjar.vscrawler.core.selector.xpath.core.function.axis;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by virjar on 17/6/6.
 * 
 * @author virjar
 * @since 0.0.1 返回下一个同胞节点(扩展) 语法 following-sibling-one
 */
public class FollowingSiblingOneFunction implements AxisFunction {
    @Override
    public Elements call(Element e) {
        Elements rs = new Elements();
        if (e.nextElementSibling() != null) {
            rs.add(e.nextElementSibling());
        }
        return rs;
    }

    @Override
    public String getName() {
        return "followingSiblingOne";
    }
}
