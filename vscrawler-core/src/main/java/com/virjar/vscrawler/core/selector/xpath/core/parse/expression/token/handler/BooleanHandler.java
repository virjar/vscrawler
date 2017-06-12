package com.virjar.vscrawler.core.selector.xpath.core.parse.expression.token.handler;

import org.apache.commons.lang3.BooleanUtils;
import org.jsoup.nodes.Element;

import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.SyntaxNode;
import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.token.Token;
import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.token.TokenHandler;
import com.virjar.vscrawler.core.selector.xpath.exception.XpathSyntaxErrorException;

/**
 * Created by virjar on 17/6/12.
 */
public class BooleanHandler implements TokenHandler {
    @Override
    public SyntaxNode parseToken(final String tokenStr) throws XpathSyntaxErrorException {
        return new SyntaxNode() {
            @Override
            public Object calc(Element element) {
                return BooleanUtils.toBoolean(tokenStr);
            }

            @Override
            public Class judeResultType() {
                return Boolean.class;
            }
        };
    }

    @Override
    public String typeName() {
        return Token.BOOLEAN;
    }
}
