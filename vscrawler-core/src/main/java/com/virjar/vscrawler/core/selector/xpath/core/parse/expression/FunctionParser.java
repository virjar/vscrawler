package com.virjar.vscrawler.core.selector.xpath.core.parse.expression;

import java.util.List;

import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.selector.xpath.core.FunctionEnv;
import com.virjar.vscrawler.core.selector.xpath.core.function.filter.FilterFunction;
import com.virjar.vscrawler.core.selector.xpath.core.parse.TokenQueue;
import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.node.FunctionNode;
import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.node.TokenNodeFactory;
import com.virjar.vscrawler.core.selector.xpath.exception.NoSuchFunctionException;
import com.virjar.vscrawler.core.selector.xpath.exception.XpathSyntaxErrorException;

/**
 * Created by virjar on 17/6/11. 对于函数,解析函数名字,参数列表,决定参数类型
 * 
 * @author virjar
 * @since 0.0.1
 */
public class FunctionParser {
    private TokenQueue tokenQueue;

    public FunctionParser(TokenQueue tokenQueue) {
        this.tokenQueue = tokenQueue;
    }

    public FunctionNode parse() throws XpathSyntaxErrorException {
        tokenQueue.consumeWhitespace();
        String functionName = tokenQueue.consumeTo("(");
        String params = tokenQueue.chompBalanced('(', ')');
        FilterFunction filterFunction = FunctionEnv.getFilterFunction(functionName);
        if (filterFunction == null) {
            throw new NoSuchFunctionException(0, "not such function:" + functionName);
        }

        List<SyntaxNode> paramList = Lists.newLinkedList();

        TokenQueue paramTokenQueue = new TokenQueue(params);
        while ((paramTokenQueue.consumeWhitespace() && !paramTokenQueue.consumeWhitespace())
                || !paramTokenQueue.isEmpty()) {

            String param;
            if (paramTokenQueue.matches("\"")) {
                param = paramTokenQueue.chompBalanced('\"', '\"');
                ExpressionParser.TokenHolder tokenHolder = new ExpressionParser.TokenHolder(TokenQueue.unescape(param),
                        ExpressionParser.TokenHolder.TokenType.CONSTANT);
                paramList.add(TokenNodeFactory.hintAndGen(tokenHolder));
            } else if (paramTokenQueue.matches("\'")) {
                param = paramTokenQueue.chompBalanced('\'', '\'');
                ExpressionParser.TokenHolder tokenHolder = new ExpressionParser.TokenHolder(TokenQueue.unescape(param),
                        ExpressionParser.TokenHolder.TokenType.CONSTANT);
                paramList.add(TokenNodeFactory.hintAndGen(tokenHolder));
            } else if (paramTokenQueue.matches("`")) {
                param = paramTokenQueue.chompBalanced('`', '`');
                ExpressionParser.TokenHolder tokenHolder = new ExpressionParser.TokenHolder(param,
                        ExpressionParser.TokenHolder.TokenType.XPATH);
                paramList.add(TokenNodeFactory.hintAndGen(tokenHolder));
            } else if (paramTokenQueue.matchesFunction()) {
                String subFunction = paramTokenQueue.consumeFunction();
                paramList.add(new FunctionParser(new TokenQueue(subFunction)).parse());
            } else if (paramTokenQueue.matches("@")) {
                paramTokenQueue.advance();
                String attributeKey = paramTokenQueue.consumeAttributeKey();
                ExpressionParser.TokenHolder tokenHolder = new ExpressionParser.TokenHolder(attributeKey,
                        ExpressionParser.TokenHolder.TokenType.ATTRIBUTE_ACTION);
                paramList.add(TokenNodeFactory.hintAndGen(tokenHolder));
            } else if (paramTokenQueue.matchesDigit()) {
                String number = paramTokenQueue.consumeDigit();
                ExpressionParser.TokenHolder tokenHolder = new ExpressionParser.TokenHolder(number,
                        ExpressionParser.TokenHolder.TokenType.NUMBER);
                paramList.add(TokenNodeFactory.hintAndGen(tokenHolder));
            } else {
                param = paramTokenQueue.consumeTo(",");

                try {
                    ExpressionParser.TokenHolder tokenHolder = new ExpressionParser.TokenHolder(param,
                            ExpressionParser.TokenHolder.TokenType.XPATH);
                    SyntaxNode syntaxNode = TokenNodeFactory.hintAndGen(tokenHolder);
                    paramList.add(syntaxNode);
                } catch (XpathSyntaxErrorException e) {
                    // 尝试当作xpath处理,如果能,则当作普通字符串常量处理
                    ExpressionParser.TokenHolder tokenHolder = new ExpressionParser.TokenHolder(param,
                            ExpressionParser.TokenHolder.TokenType.CONSTANT);
                    paramList.add(TokenNodeFactory.hintAndGen(tokenHolder));
                }
            }
        }

        return new FunctionNode(filterFunction, paramList);
    }
}
