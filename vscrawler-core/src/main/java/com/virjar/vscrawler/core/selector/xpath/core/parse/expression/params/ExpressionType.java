package com.virjar.vscrawler.core.selector.xpath.core.parse.expression.params;

import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.SyntaxNode;

import lombok.Getter;

/**
 * Created by virjar on 17/6/10.
 */
public class ExpressionType implements ParamType {
    @Getter
    private SyntaxNode syntaxNode;

    public ExpressionType(SyntaxNode syntaxNode) {
        this.syntaxNode = syntaxNode;
    }
}
