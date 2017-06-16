package com.virjar.vscrawler.samples.xpath;

import java.util.List;

import org.jsoup.nodes.Element;

import com.google.common.base.Preconditions;
import com.virjar.sipsoup.function.filter.FilterFunction;
import com.virjar.sipsoup.parse.expression.SyntaxNode;

/**
 * Created by virjar on 17/6/16. sipSoup当前版本(1.0)参数个数检查有bug,所以替换这个类
 */
public class MyContainsFunction implements FilterFunction {
    @Override
    public Object call(Element element, List<SyntaxNode> params) {
        Preconditions.checkArgument(params.size() >= 2, "contains need 2 params");
        return params.get(0).calc(element).toString().contains(params.get(1).calc(element).toString());
    }

    @Override
    public String getName() {
        return "contains";
    }
}
