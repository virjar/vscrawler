package com.virjar.vscrawler.core.selector.strfunction.syntax;

/**
 * Created by virjar on 17/7/8.
 */
public class StringSyntaxNode implements SyntaxNode {
    private String value;

    public StringSyntaxNode(String value) {
        this.value = value;
    }

    @Override
    public Object calculate(StringContext stringContext) {
        return value;
    }
}
