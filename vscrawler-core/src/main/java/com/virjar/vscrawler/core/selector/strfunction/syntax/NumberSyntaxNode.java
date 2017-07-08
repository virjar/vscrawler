package com.virjar.vscrawler.core.selector.strfunction.syntax;

/**
 * Created by virjar on 17/7/8.
 */
public class NumberSyntaxNode implements SyntaxNode {
    private Number value;

    public NumberSyntaxNode(Number value) {
        this.value = value;
    }

    @Override
    public Object calculate(StringContext stringContext) {
        return value;
    }
}
