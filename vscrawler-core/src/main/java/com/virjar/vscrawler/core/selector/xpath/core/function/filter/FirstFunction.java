package com.virjar.vscrawler.core.selector.xpath.core.function.filter;

import java.util.List;

import org.jsoup.nodes.Element;

import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.SyntaxNode;
import com.virjar.vscrawler.core.selector.xpath.util.XpathUtil;

/**
 * Created by virjar on 17/6/6.
 * 
 * @since 0.0.1
 * @author virjar 判断一个元素是不是同名同胞中的第一个
 */
public class FirstFunction implements FilterFunction {
    @Override
    public Object call(Element element, List<SyntaxNode> params) {
        return XpathUtil.getElIndexInSameTags(element) == 1;
    }

    @Override
    public String getName() {
        return "first";
    }
}
