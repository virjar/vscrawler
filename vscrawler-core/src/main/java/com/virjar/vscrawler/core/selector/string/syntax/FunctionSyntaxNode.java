package com.virjar.vscrawler.core.selector.string.syntax;

import java.util.List;

import com.virjar.vscrawler.core.selector.string.function.StringFunction;

/**
 * Created by virjar on 17/7/8.
 */
public class FunctionSyntaxNode implements SyntaxNode {
    private StringFunction stringFunction;
    private List<SyntaxNode> params;

    public FunctionSyntaxNode(StringFunction stringFunction, List<SyntaxNode> params) {
        this.params = params;
        this.stringFunction = stringFunction;
    }

    @Override
    public Object calculate(StringContext stringContext) {
        return stringFunction.call(stringContext, params);
    }

    public String functionName(){
        return stringFunction.determineFunctionName();
    }
}
