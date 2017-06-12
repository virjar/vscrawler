package com.virjar.vscrawler.core.selector.xpath.core.parse.expression.token;

import java.util.Map;
import java.util.TreeSet;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.virjar.vscrawler.core.selector.xpath.core.parse.TokenQueue;
import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.OperatorEnv;
import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.token.consumer.*;
import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.token.handler.*;

/**
 * Created by virjar on 17/6/12.
 */
public class TokenAnalysisRegistry {
    private static TreeSet<TokenConsumerWrapper> allConsumer = Sets.newTreeSet();
    private static Map<String, TokenHandler> allHandler = Maps.newHashMap();

    static {
        registerHandler(new AttributeHandler());
        registerHandler(new BooleanHandler());
        registerHandler(new ConstantHandler());
        registerHandler(new FunctionHandler());
        registerHandler(new NumberHandler());
        registerHandler(new XpathHandler());
        registerHandler(new ExpressionHandler());

        registerConsumer(new AttributeActionConsumer());
        registerConsumer(new BooleanConsumer());
        registerConsumer(new StringConstantConsumer());
        registerConsumer(new FunctionConsumer());
        registerConsumer(new DigitConsumer());
        registerConsumer(new XpathConsumer());
        registerConsumer(new OperatorConsumer());
        registerConsumer(new ExpressionConsumer());

        // TODO
        registerConsumer(new DefaultWordConsumer());
        registerConsumer(new DefaultXpathConsumer());
    }

    public static void registerHandler(TokenHandler tokenHandler) {
        allHandler.put(tokenHandler.typeName(), tokenHandler);
    }

    /**
     * @see OperatorEnv#addOperator(java.lang.String, int, java.lang.Class)
     * @param tokenConsumer token识别器
     */
    public static void registerConsumer(TokenConsumer tokenConsumer) {
        // operator是特殊逻辑,他应该由系统解析,外部不能知道如何构建语法树,所以操作符的语法节点管理权由框架持有,
        // 第三方如需扩展,可以通过扩展操作符的方式,注册操作符的运算逻辑即可
        if (!Token.OPERATOR.equals(tokenConsumer.tokenType()) && !allHandler.containsKey(tokenConsumer.tokenType())) {
            throw new IllegalStateException("can not register token consumer ,not token handler available");
        }
        allConsumer.add(new TokenConsumerWrapper(tokenConsumer));
    }

    public static TokenHandler findHandler(String tokenType) {
        return allHandler.get(tokenType);
    }

    public static Iterable<? extends TokenConsumer> consumerIterable() {
        return allConsumer;
    }

    private static class TokenConsumerWrapper implements Comparable<TokenConsumer>, TokenConsumer {
        private TokenConsumer delegate;

        TokenConsumerWrapper(TokenConsumer delegate) {
            this.delegate = delegate;
        }

        @Override
        public String consume(TokenQueue tokenQueue) {
            return delegate.consume(tokenQueue);
        }

        @Override
        public int order() {
            return delegate.order();
        }

        @Override
        public String tokenType() {
            return delegate.tokenType();
        }

        @Override
        public int compareTo(TokenConsumer o) {
            return Integer.valueOf(delegate.order()).compareTo(o.order());
        }
    }
}
