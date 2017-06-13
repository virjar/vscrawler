package com.virjar.vscrawler.core.selector.xpath.function.axis;

import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by virjar on 17/6/6.
 * 
 * @author virjar
 * @since 0.0.1 全部子代节点和自身
 */
public class DescendantOrSelfFunction implements AxisFunction {
    @Override
    public Elements call(Element e, List<String> args) {
        Elements rs = e.getAllElements();
        rs.add(e);
        return rs;
    }

    @Override
    public String getName() {
        return "descendantOrSelf";
    }
}
