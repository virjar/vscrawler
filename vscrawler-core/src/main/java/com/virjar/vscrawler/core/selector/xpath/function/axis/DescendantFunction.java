package com.virjar.vscrawler.core.selector.xpath.function.axis;

import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by virjar on 17/6/6.
 * 
 * @author virjar
 * @since 0.0.1 全部子代节点 儿子，孙子，孙子的儿子...
 */
public class DescendantFunction implements AxisFunction {
    @Override
    public Elements call(Element e, List<String> args) {
        return e.getAllElements();
    }

    @Override
    public String getName() {
        return "descendant";
    }
}
