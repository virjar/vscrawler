package com.virjar.vscrawler.core.selector.xpath.parse.expression.node;

import com.virjar.vscrawler.core.selector.xpath.parse.expression.OperatorEnv;
import com.virjar.vscrawler.core.selector.xpath.parse.expression.SyntaxNode;

/**
 * Created by virjar on 17/6/10.
 */
public abstract class WrapperUnit extends AlgorithmUnit {
    private AlgorithmUnit delegate = null;

    protected abstract String targetName();

    @Override
    public void setLeft(SyntaxNode left) {
        super.setLeft(left);
    }

    @Override
    public void setRight(SyntaxNode right) {
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
