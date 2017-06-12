package com.virjar.vscrawler.core.selector.xpath.core.parse.expression.token.consumer;

import com.virjar.vscrawler.core.selector.xpath.core.parse.TokenQueue;
import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.token.Token;
import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.token.TokenConsumer;

/**
 * Created by virjar on 17/6/12.
 */
public class FunctionConsumer implements TokenConsumer {
    @Override
    public String consume(TokenQueue tokenQueue) {
        if (tokenQueue.matchesFunction()) {
            return tokenQueue.consumeFunction();
        }
        return null;
    }

    @Override
    public int order() {
        return 60;
    }

    @Override
    public String tokenType() {
        return Token.FUNCTION;
    }
}
