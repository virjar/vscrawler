package com.virjar.vscrawler.core.selector.xpath.parse.expression.token.consumer;

import com.virjar.vscrawler.core.selector.xpath.parse.TokenQueue;
import com.virjar.vscrawler.core.selector.xpath.parse.expression.token.Token;
import com.virjar.vscrawler.core.selector.xpath.parse.expression.token.TokenConsumer;

/**
 * Created by virjar on 17/6/12.
 */
public class StringConstantConsumer implements TokenConsumer {
    @Override
    public String consume(TokenQueue tokenQueue) {
        if (tokenQueue.matches("\'")) {
            return TokenQueue.unescape(tokenQueue.chompBalanced('\'', '\''));
        } else if (tokenQueue.matches("\"")) {
            return TokenQueue.unescape(tokenQueue.chompBalanced('\"', '\"'));
        }
        return null;
    }

    @Override
    public int order() {
        return 30;
    }

    @Override
    public String tokenType() {
        return Token.CONSTANT;
    }
}
