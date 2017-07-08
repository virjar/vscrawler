package com.virjar.vscrawler.core.selector.strfunction.syntax;

import com.virjar.vscrawler.core.selector.strfunction.operator.Operation;

/**
 * Created by virjar on 17/7/8.
 */
public class OperatorSyntaxNode implements SyntaxNode {
    private SyntaxNode left;
    private SyntaxNode right;
    private Operation operation;

    public OperatorSyntaxNode(SyntaxNode left, SyntaxNode right, Operation operation) {
        this.left = left;
        this.operation = operation;
        this.right = right;
    }

    @Override
    public Object calculate(StringContext stringContext) {
        return operation.operate(left, right, stringContext);
    }
}
