package com.virjar.vscrawler.core.selector.xpath.core.parse.expression.node;

import java.util.List;

import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.SyntaxNode;
import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.params.ParamType;
import com.virjar.vscrawler.core.selector.xpath.core.function.filter.FilterFunction;
import com.virjar.vscrawler.core.selector.xpath.model.JXNode;

/**
 * Created by virjar on 17/6/10.
 */
public class FunctionNode implements SyntaxNode {
    private FilterFunction filterFunction;
    private List<ParamType> filterFunctionParams;

    public FunctionNode(FilterFunction filterFunction, List<ParamType> filterFunctionParams) {
        this.filterFunction = filterFunction;
        this.filterFunctionParams = filterFunctionParams;
    }

    @Override
    public Object calc(JXNode jxNode) {
        return filterFunction.call(jxNode.getElement(), filterFunctionParams);
    }

    @Override
    public Class judeResultType() {
        return null;// TODO
    }
}
