package com.virjar.vscrawler.core.selector.xpath.core.function.filter;

import org.jsoup.nodes.Element;

/**
 * Created by virjar on 17/6/6.
 * 
 * @author virjar
 * @since 0.0.1 获取元素自己的子文本
 */
public class TextFunction implements FilterFunction {
    @Override
    public Object call(Element element) {
        return element.ownText();
    }

    @Override
    public String getName() {
        return "text";
    }
}
