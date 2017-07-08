package com.virjar.vscrawler.core.selector.string;

import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.virjar.vscrawler.core.selector.string.function.StringFunction;
import com.virjar.vscrawler.core.selector.string.function.StringFunctionEnv;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.virjar.vscrawler.core.selector.string.operator.*;
import com.virjar.vscrawler.core.selector.string.syntax.*;

/**
 * Created by virjar on 17/7/8.
 */
public class ExpressionParser {

    private StringFunctionTokenQueue stringFunctionTokenQueue;

    public ExpressionParser(StringFunctionTokenQueue stringFunctionTokenQueue) {
        this.stringFunctionTokenQueue = stringFunctionTokenQueue;
    }

    public SyntaxNode parse() {
        // token分析
        List<TokenHolder> tokenStream = tokenStream();

        // 转后缀表达式
        // 构建逆波兰式
        Stack<TokenHolder> stack = new Stack<>();
        // RPN就是逆波兰式的含义
        List<TokenHolder> RPN = Lists.newLinkedList();
        TokenHolder bottom = new TokenHolder("#", TokenType.Operator);
        bottom.data = "#";
        stack.push(bottom);

        for (TokenHolder tokenHolder : tokenStream) {
            if (tokenHolder.tokenType.equals(TokenType.Operator)) {
                TokenHolder preSymbol = stack.peek();
                while (compareSymbolPripority(tokenHolder, preSymbol) <= 0) {
                    RPN.add(preSymbol);
                    stack.pop();
                    preSymbol = stack.peek();
                }
                stack.push(tokenHolder);
            } else {
                RPN.add(tokenHolder);

            }
        }
        while (true) {
            TokenHolder peek = stack.peek();
            if (TokenType.Operator.equals(peek.tokenType) && peek.data.equals("#")) {
                break;
            }
            RPN.add(stack.pop());
        }

        // 构建计算树
        Stack<SyntaxNode> computeStack = new Stack<>();

        for (TokenHolder tokenHolder : RPN) {
            if (tokenHolder.tokenType.equals(TokenType.Operator)) {
                SyntaxNode right = computeStack.pop();
                SyntaxNode left = computeStack.pop();
                computeStack.push(buildAlgorithmUnit(tokenHolder, left, right));
            } else {
                computeStack.push(buildByTokenHolder(tokenHolder));
            }
        }
        return computeStack.pop();
    }

    private SyntaxNode buildByTokenHolder(TokenHolder tokenHolder) {
        if (tokenHolder.tokenType == TokenType.Expression) {
            return new ExpressionParser(new StringFunctionTokenQueue(tokenHolder.data)).parse();
        }
        if (tokenHolder.tokenType == TokenType.Function) {
            return parseFunction(new StringFunctionTokenQueue(tokenHolder.data));
        }
        if (tokenHolder.tokenType == TokenType.String) {
            return new StringSyntaxNode(tokenHolder.data);
        }
        if (tokenHolder.tokenType == TokenType.Boolean) {
            return new BooleanSyntaxNode(BooleanUtils.toBoolean(tokenHolder.data));
        }
        if (tokenHolder.tokenType == TokenType.Number) {
            if (tokenHolder.data.contains(".")) {
                return new NumberSyntaxNode(NumberUtils.toDouble(tokenHolder.data));
            } else {
                return new NumberSyntaxNode(NumberUtils.toInt(tokenHolder.data));
            }
        }
        throw new IllegalStateException("unknown token type: " + tokenHolder.tokenType);
    }

    private SyntaxNode buildAlgorithmUnit(TokenHolder tokenHolder, SyntaxNode left, SyntaxNode right) {
        return new OperatorSyntaxNode(left, right, operationMap.get(tokenHolder.data));
    }

    private static Map<String, Integer> proproityMap = Maps.newHashMap();
    private static Map<String, Operation> operationMap = Maps.newHashMap();
    static {
        proproityMap.put("*", 3);
        proproityMap.put("/", 3);
        proproityMap.put("%", 3);
        proproityMap.put("+", 2);
        proproityMap.put("-", 2);
        proproityMap.put("#", 0);

        operationMap.put("+", new Add());
        operationMap.put("-", new Minus());
        operationMap.put("*", new Multi());
        operationMap.put("/", new Divide());
        operationMap.put("%", new Remainder());
    }

    private int compareSymbolPripority(TokenHolder tokenHolder, TokenHolder preSymbol) {
        return proproityMap.get(tokenHolder.data).compareTo(proproityMap.get(preSymbol.data));
    }

    private List<TokenHolder> tokenStream() {
        stringFunctionTokenQueue.consumeWhitespace();
        List<TokenHolder> ret = Lists.newLinkedList();
        while (!stringFunctionTokenQueue.isEmpty()) {
            if (stringFunctionTokenQueue.matchesFunction()) {
                ret.add(new TokenHolder(stringFunctionTokenQueue.consumeFunction(), TokenType.Function));
            } else if (stringFunctionTokenQueue.matches("(")) {
                String subExpression = stringFunctionTokenQueue.chompBalanced('(', ')');
                if (subExpression == null) {
                    throw new IllegalStateException(
                            "can not parse token :" + stringFunctionTokenQueue.remainder() + " ,unmatched quote");
                }
                ret.add(new TokenHolder(subExpression, TokenType.Expression));
            } else if (stringFunctionTokenQueue.matchesBoolean()) {
                ret.add(new TokenHolder(stringFunctionTokenQueue.consumeWord(), TokenType.Boolean));
            } else if (stringFunctionTokenQueue.matchesAny("+", "-", "*", "/", "%")) {
                char peek = stringFunctionTokenQueue.peek();
                stringFunctionTokenQueue.advance();
                ret.add(new TokenHolder(String.valueOf(peek), TokenType.Operator));
            } else if (stringFunctionTokenQueue.matchesDigit()) {
                ret.add(new TokenHolder(stringFunctionTokenQueue.consumeDigit(), TokenType.Number));
            } else if (stringFunctionTokenQueue.matchesAny('\"', '\'')) {
                String str;
                if (stringFunctionTokenQueue.peek() == '\"') {
                    str = stringFunctionTokenQueue.chompBalanced('\"', '\"');
                } else {
                    str = stringFunctionTokenQueue.chompBalanced('\'', '\'');
                }
                ret.add(new TokenHolder(StringFunctionTokenQueue.unescape(str), TokenType.String));
            } else {
                throw new IllegalStateException("unknown token:" + stringFunctionTokenQueue.remainder());
            }
            stringFunctionTokenQueue.consumeWhitespace();
        }
        return ret;
    }

    private static class TokenHolder {
        TokenType tokenType;
        String data;

        TokenHolder(String data, TokenType tokenType) {
            this.data = data;
            this.tokenType = tokenType;
        }
    }

    private enum TokenType {
        Function, Expression, Number, Operator, Boolean, String
    }

    private SyntaxNode parseFunction(StringFunctionTokenQueue tokenQueue) {
        String functionString = tokenQueue.consumeFunction();
        StringFunctionTokenQueue tempTokenQueue = new StringFunctionTokenQueue(functionString);
        String functionName = tempTokenQueue.consumeIdentify();
        StringFunction function = StringFunctionEnv.findFunction(functionName);
        if (function == null) {
            throw new IllegalStateException("not such function: " + functionName);
        }
        tempTokenQueue.consumeWhitespace();
        if (tempTokenQueue.isEmpty() || tempTokenQueue.peek() != '(') {
            throw new IllegalStateException(
                    "can not parse token: " + functionString + "  ,it is not same to a function");
        }

        String paramsStr = tempTokenQueue.chompBalanced('(', ')');

        StringFunctionTokenQueue paramTokenQueue = new StringFunctionTokenQueue(paramsStr);

        String parameter;
        List<SyntaxNode> params = Lists.newLinkedList();
        while ((parameter = paramTokenQueue.consumeIgnoreQuote(',')) != null) {
            params.add(new ExpressionParser(new StringFunctionTokenQueue(parameter)).parse());
        }
        return new FunctionSyntaxNode(function, params);
    }
}
