package com.virjar.vscrawler.core.selector.xpath.core.parse.expression;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.selector.xpath.core.FunctionEnv;
import com.virjar.vscrawler.core.selector.xpath.core.function.filter.FilterFunction;
import com.virjar.vscrawler.core.selector.xpath.core.parse.TokenQueue;
import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.node.FunctionNode;
import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.token.Token;
import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.token.TokenAnalysisRegistry;
import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.token.TokenConsumer;
import com.virjar.vscrawler.core.selector.xpath.exception.NoSuchFunctionException;
import com.virjar.vscrawler.core.selector.xpath.exception.XpathSyntaxErrorException;

/**
 * Created by virjar on 17/6/11. 对于函数,解析函数名字,参数列表,决定参数类型
 * 
 * @author virjar
 * @since 0.0.1
 */
public class FunctionParser {

    private static final List<String> paramExcludeTypes = Lists.newArrayList(Token.OPERATOR);
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

            if (paramTokenQueue.peek() == ',') {
                paramTokenQueue.advance();
                paramTokenQueue.consumeWhitespace();
            }

            boolean hint = false;
            for (TokenConsumer tokenConsumer : TokenAnalysisRegistry.consumerIterable()) {
                if (excludeForParam(tokenConsumer.tokenType())) {
                    continue;
                }

                String consume = tokenConsumer.consume(paramTokenQueue);
                if (consume == null) {
                    continue;
                }
                hint = true;
                paramList.add(TokenAnalysisRegistry.findHandler(tokenConsumer.tokenType()).parseToken(consume));
                // paramList.add(TokenNodeFactory
                // .hintAndGen(new ExpressionParser.TokenHolder(consume, tokenConsumer.tokenType())));
                break;
            }

            if (hint) {
                continue;
            }

            String param = paramTokenQueue.consumeTo(",");
            if (StringUtils.isEmpty(param)) {
                continue;
            }

            try {
                // ExpressionParser.TokenHolder tokenHolder = new ExpressionParser.TokenHolder(param, Token.XPATH);
                // SyntaxNode syntaxNode = TokenNodeFactory.hintAndGen(tokenHolder);
                // paramList.add(syntaxNode);
                paramList.add(TokenAnalysisRegistry.findHandler(Token.XPATH).parseToken(param));
            } catch (XpathSyntaxErrorException e) {
                // 尝试当作xpath处理,如果能,则当作普通字符串常量处理
                // ExpressionParser.TokenHolder tokenHolder = new ExpressionParser.TokenHolder(param, Token.CONSTANT);
                // paramList.add(TokenNodeFactory.hintAndGen(tokenHolder));
                paramList.add(TokenAnalysisRegistry.findHandler(Token.CONSTANT).parseToken(param));
            }

        }

        return new FunctionNode(filterFunction, paramList);
    }

    private boolean excludeForParam(String tokenType) {
        return paramExcludeTypes.contains(tokenType);
    }
}
