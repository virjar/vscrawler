package com.virjar.vscrawler.core.selector.strfunction.syntax;

/**
 * Created by virjar on 17/7/8.
 */
public class BooleanSyntaxNode implements SyntaxNode {
    private Boolean value;

    public BooleanSyntaxNode(Boolean value) {
        this.value = value;
    }

    @Override
    public Object calculate(StringContext stringContext) {
        return value;
    }
}
