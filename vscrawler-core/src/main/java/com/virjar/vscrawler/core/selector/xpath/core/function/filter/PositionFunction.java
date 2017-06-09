package com.virjar.vscrawler.core.selector.xpath.core.function.filter;

import org.jsoup.nodes.Element;

import com.virjar.vscrawler.core.selector.xpath.util.XpathUtil;

/**
 * Created by virjar on 17/6/6.
 * 
 * @author virjar
 * @since 0.0.1 返回一个元素在同名兄弟节点中的位置
 */
public class PositionFunction implements FilterFunction {
    @Override
    public Object call(Element element) {
        return XpathUtil.getElIndexInSameTags(element);
    }

    @Override
    public String getName() {
        return "position";
    }
}
