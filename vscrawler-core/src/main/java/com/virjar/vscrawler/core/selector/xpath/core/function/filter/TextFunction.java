package com.virjar.vscrawler.core.selector.xpath.core.function.filter;

import java.util.List;

import org.jsoup.nodes.Element;

import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.params.ParamType;

/**
 * Created by virjar on 17/6/6.
 * 
 * @author virjar
 * @since 0.0.1 获取元素自己的子文本
 */
public class TextFunction implements FilterFunction {
    @Override
    public Object call(Element element, List<ParamType> params) {
        return element.ownText();
    }

    @Override
    public String getName() {
        return "text";
    }
}
