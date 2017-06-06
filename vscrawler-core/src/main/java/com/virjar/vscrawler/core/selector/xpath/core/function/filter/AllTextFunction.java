package com.virjar.vscrawler.core.selector.xpath.core.function.filter;

import org.jsoup.nodes.Element;

/**
 * Created by virjar on 17/6/6.
 * 
 * @since 0.0.1
 * @author virjar 获取元素下面的全部文本
 */
public class AllTextFunction implements FilterFunction {
    @Override
    public Object call(Element element) {
        return element.text();
    }

    @Override
    public String getName() {
        return "allText";
    }
}
