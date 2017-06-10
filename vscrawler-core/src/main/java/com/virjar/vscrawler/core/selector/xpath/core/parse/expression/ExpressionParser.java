package com.virjar.vscrawler.core.selector.xpath.core.parse.expression;

import com.virjar.vscrawler.core.selector.xpath.core.parse.TokenQueue;

/**
 * Created by virjar on 17/6/10.
 * 
 * @since 0.0.1
 * @author virjar 解析表达式,用在谓语中
 */
public class ExpressionParser {

    private TokenQueue expressionTokenQueue;

    public ExpressionParser(TokenQueue expressionTokenQueue) {
        this.expressionTokenQueue = expressionTokenQueue;
    }

    public SyntaxNode parse() {
        return null;
    }
}
