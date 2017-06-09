package com.virjar.vscrawler.core.selector.xpath.core.function.axis;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by virjar on 17/6/6.
 * 
 * @author virjar
 * @since 0.0.1 节点前面的全部同胞节点，preceding-sibling
 */
public class PrecedingSiblingFunction implements AxisFunction {
    @Override
    public Elements call(Element e, String... args) {
        Elements rs = new Elements();
        Element tmp = e.previousElementSibling();
        while (tmp != null) {
            rs.add(tmp);
            tmp = tmp.previousElementSibling();
        }
        return rs;
    }

    @Override
    public String getName() {
        return "precedingSibling";
    }
}
