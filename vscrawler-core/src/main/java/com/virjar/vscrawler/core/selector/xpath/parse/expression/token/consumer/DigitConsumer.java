package com.virjar.vscrawler.core.selector.xpath.parse.expression.token.consumer;

import com.virjar.vscrawler.core.selector.xpath.parse.TokenQueue;
import com.virjar.vscrawler.core.selector.xpath.parse.expression.token.Token;
import com.virjar.vscrawler.core.selector.xpath.parse.expression.token.TokenConsumer;

/**
 * Created by virjar on 17/6/12.
 */
public class DigitConsumer implements TokenConsumer {
    @Override
    public String consume(TokenQueue tokenQueue) {
        // 当前遇到的串是一个数字
        if (tokenQueue.matchesDigit()) {
            return tokenQueue.consumeDigit();
        }
        return null;
    }

    @Override
    public int order() {
        return 50;
    }

    @Override
    public String tokenType() {
        return Token.NUMBER;
    }
}
