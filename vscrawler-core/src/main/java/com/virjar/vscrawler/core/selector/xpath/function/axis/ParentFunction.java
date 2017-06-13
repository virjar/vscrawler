package com.virjar.vscrawler.core.selector.xpath.function.axis;

import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by virjar on 17/6/6.
 * 
 * @author virjar
 * @since 0.0.1 父节点
 */
public class ParentFunction implements AxisFunction {
    @Override
    public Elements call(Element e, List<String> args) {
        return new Elements(e.parent());
    }

    @Override
    public String getName() {
        return "parent";
    }
}
