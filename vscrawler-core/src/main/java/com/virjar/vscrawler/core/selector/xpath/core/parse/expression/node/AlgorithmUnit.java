package com.virjar.vscrawler.core.selector.xpath.core.parse.expression.node;

import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.SyntaxNode;

/**
 * Created by virjar on 17/6/10.
 */
public abstract class AlgorithmUnit implements SyntaxNode {
    protected SyntaxNode left = null;
    protected SyntaxNode right = null;

    public void setLeft(SyntaxNode left) {
        this.left = left;
    }

    public void setRight(SyntaxNode right) {
        this.right = right;
    }

}
