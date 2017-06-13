package com.virjar.vscrawler.core.selector.xpath.parse.expression.token.consumer;

import com.virjar.vscrawler.core.selector.xpath.parse.TokenQueue;
import com.virjar.vscrawler.core.selector.xpath.parse.XpathParser;
import com.virjar.vscrawler.core.selector.xpath.parse.expression.token.Token;
import com.virjar.vscrawler.core.selector.xpath.parse.expression.token.TokenConsumer;
import org.apache.commons.lang3.StringUtils;

import com.virjar.vscrawler.core.selector.xpath.exception.XpathSyntaxErrorException;

/**
 * Created by virjar on 17/6/12.
 */
public class DefaultXpathConsumer implements TokenConsumer {
    @Override
    public String consume(TokenQueue tokenQueue) {
        String s = tokenQueue.tryConsumeTo(" ");
        if (StringUtils.isEmpty(s)) {
            return null;
        }
        try {
            XpathParser.compile(s);
            return tokenQueue.consumeTo(" ");
        } catch (XpathSyntaxErrorException e) {
            return null;
        }
    }

    @Override
    public int order() {
        return 80;
    }

    @Override
    public String tokenType() {
        return Token.XPATH;
    }
}
