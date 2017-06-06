package com.virjar.vscrawler.core.selector.xpath.core.function.axis;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by virjar on 17/6/6.
 * 
 * @author virjar
 * @since 0.0.1 节点后面的全部同胞节点following-sibling
 */
public class FollowingSiblingFunction implements AxisFunction {
    @Override
    public Elements call(Element e) {
        Elements rs = new Elements();
        Element tmp = e.nextElementSibling();
        while (tmp != null) {
            rs.add(tmp);
            tmp = tmp.nextElementSibling();
        }
        return rs;
    }

    @Override
    public String getName() {
        return "followingSibling";
    }
}
