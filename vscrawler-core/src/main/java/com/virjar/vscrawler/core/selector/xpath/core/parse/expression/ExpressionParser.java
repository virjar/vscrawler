package com.virjar.vscrawler.core.selector.xpath.core.parse.expression;

import java.util.List;

import com.google.common.collect.Lists;
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
        List<TokenHolder> tokenStream = tokenStream();

        return null;
    }

    private List<TokenHolder> tokenStream() {
        List<TokenHolder> tokenStream = Lists.newLinkedList();
        expressionTokenQueue.consumeWhitespace();
        while (!expressionTokenQueue.isEmpty()) {
            // 当前遇到的串是一个括号
            if (expressionTokenQueue.matches("(")) {
                // 括号优先级,单独处理,不在逆波兰式内部处理括号问题了,这样逻辑简单一些,而且也浪费不了太大的计算消耗
                String subExpression = expressionTokenQueue.chompBalanced('(', ')');
                TokenHolder tokenHolder = new TokenHolder();
                tokenStream.add(tokenHolder);
                tokenHolder.expression = subExpression;
                tokenHolder.type = TokenHolder.TokenType.EXPRESSION;
                expressionTokenQueue.consumeWhitespace();
                continue;
            }

            // 当前遇到的串是一个数字
            if (expressionTokenQueue.matchesDigit()) {
                String number = expressionTokenQueue.consumeDigit();
                TokenHolder tokenHolder = new TokenHolder();
                tokenStream.add(tokenHolder);
                tokenHolder.type = TokenHolder.TokenType.NUMBER;
                tokenHolder.expression = number;
                expressionTokenQueue.consumeWhitespace();
                continue;
            }

            // 取属性动作
            if (expressionTokenQueue.matches("@")) {
                expressionTokenQueue.consume();
                String attributeKey = expressionTokenQueue.consumeAttributeKey();
                TokenHolder tokenHolder = new TokenHolder();
                tokenStream.add(tokenHolder);
                tokenHolder.type = TokenHolder.TokenType.ATTRIBUTE_ACTION;
                tokenHolder.expression = attributeKey;
                expressionTokenQueue.consumeWhitespace();
                continue;
            }

            // xpath子串
            if (expressionTokenQueue.matches("`")) {
                String xpathStr = expressionTokenQueue.chompBalanced('`', '`');
                TokenHolder tokenHolder = new TokenHolder();
                tokenStream.add(tokenHolder);
                tokenHolder.type = TokenHolder.TokenType.XPATH;
                tokenHolder.expression = xpathStr;
                expressionTokenQueue.consumeWhitespace();
                continue;
            }

            // 字符串常量
            if (expressionTokenQueue.matches("'")) {
                String subStr = expressionTokenQueue.chompBalanced('\'', '\'');
                TokenHolder tokenHolder = new TokenHolder();
                tokenStream.add(tokenHolder);
                tokenHolder.type = TokenHolder.TokenType.CONSTANT;
                tokenHolder.expression = subStr;
                expressionTokenQueue.consumeWhitespace();
                continue;
            }

            // 字符串常量
            if (expressionTokenQueue.matches("\"")) {
                String subStr = expressionTokenQueue.chompBalanced('\"', '\"');
                TokenHolder tokenHolder = new TokenHolder();
                tokenStream.add(tokenHolder);
                tokenHolder.type = TokenHolder.TokenType.CONSTANT;
                tokenHolder.expression = subStr;
                expressionTokenQueue.consumeWhitespace();
                continue;
            }

            // 运算符

            // 兜底

            // 不成功,报错
        }

        return tokenStream;
    }

    private static class TokenHolder {
        enum TokenType {
            SYMBOL, CONSTANT, NUMBER, EXPRESSION, ATTRIBUTE_ACTION, XPATH;
        }

        TokenType type;
        String expression;

        @Override
        public String toString() {
            return expression;
        }
    }
}
