package com.virjar.vscrawler.core.selector.xpath.core.parse.expression.node;

import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.OperatorEnv;
import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.SyntaxNode;

/**
 * Created by virjar on 17/6/10.
 */
public abstract class WrapperUnit extends AlgorithmUnit {
    private AlgorithmUnit delegate = null;

    abstract String targetName();

    @Override
    protected void setLeft(SyntaxNode left) {
        super.setLeft(left);
    }

    @Override
    protected void setRight(SyntaxNode right) {
        super.setRight(right);
    }

    protected AlgorithmUnit wrap() {
        if (delegate == null) {
            delegate = OperatorEnv.createByName(targetName());
            delegate.setLeft(left);
            delegate.setRight(right);
        }
        return delegate;
    }
}
