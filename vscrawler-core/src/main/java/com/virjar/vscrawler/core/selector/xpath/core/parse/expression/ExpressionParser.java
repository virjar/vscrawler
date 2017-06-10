package com.virjar.vscrawler.core.selector.xpath.core.parse.expression;

import java.util.List;

import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.selector.xpath.core.parse.TokenQueue;
import com.virjar.vscrawler.core.selector.xpath.exception.XpathSyntaxErrorException;

/**
 * Created by virjar on 17/6/10.
 * 
 * @since 0.0.1
 * @author virjar 解析表达式,用在谓语中
 */
public class ExpressionParser {

    public static void main(String[] args) throws XpathSyntaxErrorException {
        String testXpath = "add(toInt(`/div[@class~='test' && @id='testid']/a/@href`),     50)-10 =0";
        List<TokenHolder> tokenHolders = new ExpressionParser(new TokenQueue(testXpath)).tokenStream();
        for (TokenHolder tokenHolder : tokenHolders) {
            System.out.println(tokenHolder.expression);
        }

        testXpath = "@class~='test' &&      @id='testid'";
        tokenHolders = new ExpressionParser(new TokenQueue(testXpath)).tokenStream();
        for (TokenHolder tokenHolder : tokenHolders) {
            System.out.println(tokenHolder.expression);
        }
    }

    private TokenQueue expressionTokenQueue;

    public ExpressionParser(TokenQueue expressionTokenQueue) {
        this.expressionTokenQueue = expressionTokenQueue;
    }

    public SyntaxNode parse() throws XpathSyntaxErrorException {
        List<TokenHolder> tokenStream = tokenStream();

        return null;
    }

    private boolean testExpression(List<TokenHolder> tokenStream) {
        // 当前遇到的串是一个括号
        if (expressionTokenQueue.matches("(")) {
            // 括号优先级,单独处理,不在逆波兰式内部处理括号问题了,这样逻辑简单一些,而且也浪费不了太大的计算消耗
            String subExpression = expressionTokenQueue.chompBalanced('(', ')');
            TokenHolder tokenHolder = new TokenHolder();
            tokenStream.add(tokenHolder);
            tokenHolder.expression = subExpression;
            tokenHolder.type = TokenHolder.TokenType.EXPRESSION;
            return true;
        }
        return false;
    }

    private boolean testDigit(List<TokenHolder> tokenStream) {
        // 当前遇到的串是一个数字
        if (expressionTokenQueue.matchesDigit()) {
            String number = expressionTokenQueue.consumeDigit();
            TokenHolder tokenHolder = new TokenHolder();
            tokenStream.add(tokenHolder);
            tokenHolder.type = TokenHolder.TokenType.NUMBER;
            tokenHolder.expression = number;
            return true;
        }
        return false;
    }

    private boolean testAttributeAction(List<TokenHolder> tokenStream) {
        // 取属性动作
        if (expressionTokenQueue.matches("@")) {
            expressionTokenQueue.advance();
            String attributeKey = expressionTokenQueue.consumeAttributeKey();
            TokenHolder tokenHolder = new TokenHolder();
            tokenStream.add(tokenHolder);
            tokenHolder.type = TokenHolder.TokenType.ATTRIBUTE_ACTION;
            tokenHolder.expression = attributeKey;
            return true;
        }
        return false;
    }

    private boolean testXpath(List<TokenHolder> tokenStream) {
        // xpath子串
        if (expressionTokenQueue.matches("`")) {
            String xpathStr = expressionTokenQueue.chompBalanced('`', '`');
            TokenHolder tokenHolder = new TokenHolder();
            tokenStream.add(tokenHolder);
            tokenHolder.type = TokenHolder.TokenType.XPATH;
            tokenHolder.expression = xpathStr;
            return true;
        }
        return false;
    }

    public boolean testStringConstant(List<TokenHolder> tokenStream, char flag) {
        // 字符串常量
        if (flag != '\'' && flag != '\"') {
            return false;
        }
        if (expressionTokenQueue.matches(String.valueOf(flag))) {
            String subStr = expressionTokenQueue.chompBalanced(flag, flag);
            TokenHolder tokenHolder = new TokenHolder();
            tokenStream.add(tokenHolder);
            tokenHolder.type = TokenHolder.TokenType.CONSTANT;
            tokenHolder.expression = TokenQueue.unescape(subStr);
            return true;
        }
        return false;
    }

    private boolean testOperator(List<TokenHolder> tokenStream) {
        // 运算符
        // 所有支持的运算符,而且是排序好了的
        List<OperatorEnv.AlgorithmHolder> algorithmHolders = OperatorEnv.allAlgorithmUnitList();
        for (OperatorEnv.AlgorithmHolder holder : algorithmHolders) {
            if (expressionTokenQueue.matches(holder.getKey())) {
                expressionTokenQueue.consume(holder.getKey());
                TokenHolder tokenHolder = new TokenHolder();
                tokenStream.add(tokenHolder);
                tokenHolder.type = TokenHolder.TokenType.SYMBOL;
                tokenHolder.expression = holder.getKey();
                return true;
            }
        }
        return false;
    }

    private boolean testFunction(List<TokenHolder> tokenStream) {
        if (expressionTokenQueue.matchesFunction()) {
            String function = expressionTokenQueue.consumeFunction();
            TokenHolder tokenHolder = new TokenHolder();
            tokenStream.add(tokenHolder);
            tokenHolder.type = TokenHolder.TokenType.FUNCTION;
            tokenHolder.expression = function;
            return true;
        }
        return false;
    }

    private List<TokenHolder> tokenStream() throws XpathSyntaxErrorException {
        List<TokenHolder> tokenStream = Lists.newLinkedList();
        // java不支持逗号表达式,这么做达到了逗号表达式的效果
        while ((expressionTokenQueue.consumeWhitespace() || !expressionTokenQueue.consumeWhitespace())
                && !expressionTokenQueue.isEmpty()) {
            if (testExpression(tokenStream)) {
                continue;
            }

            if (testAttributeAction(tokenStream)) {
                continue;
            }
            if (testXpath(tokenStream)) {
                continue;
            }

            if (testStringConstant(tokenStream, '\'')) {
                continue;
            }
            if (testStringConstant(tokenStream, '\"')) {
                continue;
            }

            if (testOperator(tokenStream)) {
                continue;
            }

            // 数字需要在操作符之后,因为先解析减号再解析负数
            if (testDigit(tokenStream)) {
                continue;
            }

            // 函数
            if (testFunction(tokenStream)) {
                continue;
            }

            // 兜底

            // 不成功,报错
            throw new XpathSyntaxErrorException(expressionTokenQueue.nowPosition(), "can not parse predicate"
                    + expressionTokenQueue.getQueue() + "  for token " + expressionTokenQueue.remainder());
        }

        return tokenStream;
    }

    private static class TokenHolder {
        enum TokenType {
            SYMBOL, CONSTANT, NUMBER, EXPRESSION, ATTRIBUTE_ACTION, XPATH, FUNCTION
        }

        TokenType type;
        String expression;

        @Override
        public String toString() {
            return expression;
        }
    }
}
