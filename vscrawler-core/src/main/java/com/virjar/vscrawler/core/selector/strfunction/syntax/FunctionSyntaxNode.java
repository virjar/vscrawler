package com.virjar.vscrawler.core.selector.strfunction.syntax;

import java.util.List;

import com.virjar.vscrawler.core.selector.strfunction.Strings;
import com.virjar.vscrawler.core.selector.strfunction.function.StringFunction;

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

    public Strings call(StringContext stringContext) {
        return stringFunction.call(stringContext, params);
    }

    @Override
    public Object calculate(StringContext stringContext) {
        return call(stringContext);
    }
}
