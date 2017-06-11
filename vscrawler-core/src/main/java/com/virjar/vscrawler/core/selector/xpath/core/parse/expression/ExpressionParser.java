package com.virjar.vscrawler.core.selector.xpath.core.parse.expression;

import java.util.List;
import java.util.Stack;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.selector.xpath.core.parse.TokenQueue;
import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.node.AlgorithmUnit;
import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.node.TokenNodeFactory;
import com.virjar.vscrawler.core.selector.xpath.exception.XpathSyntaxErrorException;

import lombok.Getter;

/**
 * Created by virjar on 17/6/10.
 *
 * @author virjar 解析表达式,用在谓语中
 * @since 0.0.1
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
        // 表达式拆分成token流
        List<TokenHolder> tokenStream = tokenStream();

        // 构建逆波兰式
        Stack<TokenHolder> stack = new Stack<>();
        // RPN就是逆波兰式的含义
        List<TokenHolder> RPN = Lists.newLinkedList();
        TokenHolder bottom = new TokenHolder("#", null);
        bottom.expression = "#";
        stack.push(bottom);

        for (TokenHolder tokenHolder : tokenStream) {
            if (tokenHolder.type != TokenHolder.TokenType.OPERATOR) {
                RPN.add(tokenHolder);
            } else {
                TokenHolder preSymbol = stack.peek();
                while (compareSymbolPripority(tokenHolder, preSymbol) <= 0) {
                    RPN.add(preSymbol);
                    stack.pop();
                    preSymbol = stack.peek();
                }
                stack.push(tokenHolder);
            }
        }
        while (!stack.peek().expression.equals("#")) {
            RPN.add(stack.pop());
        }

        // 构建计算树
        Stack<SyntaxNode> computeStack = new Stack<>();

        for (TokenHolder tokenHolder : RPN) {
            if (tokenHolder.type != TokenHolder.TokenType.OPERATOR) {
                computeStack.push(buildByTokenHolder(tokenHolder));
            } else {
                SyntaxNode right = computeStack.pop();
                SyntaxNode left = computeStack.pop();
                computeStack.push(buildAlgorithmUnit(tokenHolder, left, right));
            }
        }
        return computeStack.pop();
    }

    private SyntaxNode buildAlgorithmUnit(TokenHolder tokenHolder, SyntaxNode left, SyntaxNode right) {
        // 对于计算树,属于内部节点,需要附加左右操作树,不能单纯根据token信息产生节点
        Preconditions.checkArgument(tokenHolder.type == TokenHolder.TokenType.OPERATOR);
        AlgorithmUnit algorithmUnit = OperatorEnv.createByName(tokenHolder.expression);
        algorithmUnit.setLeft(left);
        algorithmUnit.setRight(right);
        return algorithmUnit;
    }

    /**
     * 非操作符的节点构建,如函数,xpath表达式,常量,数字等,他们的构造方法和计算树无关,是表达式里面最原始的计算叶节点
     *
     * @param tokenHolder token数据
     * @return 用来挂在计算树上面的叶节点
     */
    private SyntaxNode buildByTokenHolder(TokenHolder tokenHolder) throws XpathSyntaxErrorException {
        Preconditions.checkArgument(tokenHolder.type != TokenHolder.TokenType.OPERATOR);
        return TokenNodeFactory.hintAndGen(tokenHolder);
    }

    private int compareSymbolPripority(TokenHolder first, TokenHolder second) {
        return OperatorEnv.judgePriority(first.expression) - OperatorEnv.judgePriority(second.expression);
    }

    private boolean testExpression(List<TokenHolder> tokenStream) {
        // 当前遇到的串是一个括号
        if (expressionTokenQueue.matches("(")) {
            // 括号优先级,单独处理,不在逆波兰式内部处理括号问题了,这样逻辑简单一些,而且也浪费不了太大的计算消耗
            String subExpression = expressionTokenQueue.chompBalanced('(', ')');
            TokenHolder tokenHolder = new TokenHolder(subExpression, TokenHolder.TokenType.EXPRESSION);
            tokenStream.add(tokenHolder);
            return true;
        }
        return false;
    }

    private boolean testDigit(List<TokenHolder> tokenStream) {
        // 当前遇到的串是一个数字
        if (expressionTokenQueue.matchesDigit()) {
            String number = expressionTokenQueue.consumeDigit();
            TokenHolder tokenHolder = new TokenHolder(number, TokenHolder.TokenType.NUMBER);
            tokenStream.add(tokenHolder);
            return true;
        }
        return false;
    }

    private boolean testAttributeAction(List<TokenHolder> tokenStream) {
        // 取属性动作
        if (expressionTokenQueue.matches("@")) {
            expressionTokenQueue.advance();
            String attributeKey = expressionTokenQueue.consumeAttributeKey();
            TokenHolder tokenHolder = new TokenHolder(attributeKey, TokenHolder.TokenType.ATTRIBUTE_ACTION);
            tokenStream.add(tokenHolder);
            return true;
        }
        return false;
    }

    private boolean testXpath(List<TokenHolder> tokenStream) {
        // xpath子串
        if (expressionTokenQueue.matches("`")) {
            String xpathStr = expressionTokenQueue.chompBalanced('`', '`');
            TokenHolder tokenHolder = new TokenHolder(xpathStr, TokenHolder.TokenType.XPATH);
            tokenStream.add(tokenHolder);
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
            TokenHolder tokenHolder = new TokenHolder(TokenQueue.unescape(subStr), TokenHolder.TokenType.CONSTANT);
            tokenStream.add(tokenHolder);
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
                TokenHolder tokenHolder = new TokenHolder(holder.getKey(), TokenHolder.TokenType.OPERATOR);
                tokenStream.add(tokenHolder);
                return true;
            }
        }
        return false;
    }

    private boolean testFunction(List<TokenHolder> tokenStream) {
        if (expressionTokenQueue.matchesFunction()) {
            String function = expressionTokenQueue.consumeFunction();
            TokenHolder tokenHolder = new TokenHolder(function, TokenHolder.TokenType.FUNCTION);
            tokenStream.add(tokenHolder);
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

    public static class TokenHolder {
        public enum TokenType {
            OPERATOR, CONSTANT, NUMBER, EXPRESSION, ATTRIBUTE_ACTION, XPATH, FUNCTION
        }

        public TokenHolder(String expression, TokenType type) {
            this.expression = expression;
            this.type = type;
        }

        @Getter
        private TokenType type;
        @Getter
        private String expression;

        @Override
        public String toString() {
            return expression;
        }
    }
}
