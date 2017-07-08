package com.virjar.vscrawler.core.selector.strfunction;

import com.virjar.vscrawler.core.selector.strfunction.syntax.FunctionSyntaxNode;
import com.virjar.vscrawler.core.selector.strfunction.syntax.SyntaxNode;

/**
 * Created by virjar on 17/7/1.
 */
public class FunctionParser {

    private String exp;

    public FunctionParser(String exp) {
        this.exp = exp;
    }

    // 将一个函数规则文本编译成模型
    public FunctionSyntaxNode parse() {
        SyntaxNode syntaxNode = new ExpressionParser(new StringFunctionTokenQueue(exp)).parse();
        if (syntaxNode instanceof FunctionSyntaxNode) {
            return (FunctionSyntaxNode) syntaxNode;
        }
        throw new IllegalStateException("the expression not same to be a function:" + exp);
    }
}
