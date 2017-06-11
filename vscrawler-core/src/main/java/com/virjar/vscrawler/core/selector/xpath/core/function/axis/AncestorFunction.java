package com.virjar.vscrawler.core.selector.xpath.core.function.axis;

import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by virjar on 17/6/6.
 * 
 * @author virjar
 * @since 0.0.1 全部祖先节点 父亲，爷爷 ， 爷爷的父亲...
 * 
 */
public class AncestorFunction implements AxisFunction {
    @Override
    public Elements call(Element e, List<String> args) {
        return e.parents();
    }

    @Override
    public String getName() {
        return "ancestor";
    }
}
