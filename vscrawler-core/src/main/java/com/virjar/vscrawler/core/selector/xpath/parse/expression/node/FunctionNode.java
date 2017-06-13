package com.virjar.vscrawler.core.selector.xpath.parse.expression.node;

import java.util.List;

import com.virjar.vscrawler.core.selector.xpath.function.filter.FilterFunction;
import com.virjar.vscrawler.core.selector.xpath.parse.expression.SyntaxNode;
import org.jsoup.nodes.Element;

/**
 * Created by virjar on 17/6/10.
 */
public class FunctionNode implements SyntaxNode {
    private FilterFunction filterFunction;
    private List<SyntaxNode> filterFunctionParams;

    public FunctionNode(FilterFunction filterFunction, List<SyntaxNode> filterFunctionParams) {
        this.filterFunction = filterFunction;
        this.filterFunctionParams = filterFunctionParams;
    }

    @Override
    public Object calc(Element element) {
        return filterFunction.call(element, filterFunctionParams);
    }

    @Override
    public Class judeResultType() {
        return null;// TODO
    }
}
