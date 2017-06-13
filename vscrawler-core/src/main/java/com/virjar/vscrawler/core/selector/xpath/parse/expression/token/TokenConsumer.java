package com.virjar.vscrawler.core.selector.xpath.parse.expression.token;

import com.virjar.vscrawler.core.selector.xpath.parse.TokenQueue;

/**
 * Created by virjar on 17/6/12.<br/>
 * 消费一个token
 */
public interface TokenConsumer {
    String consume(TokenQueue tokenQueue);

    int order();

    String tokenType();
}
