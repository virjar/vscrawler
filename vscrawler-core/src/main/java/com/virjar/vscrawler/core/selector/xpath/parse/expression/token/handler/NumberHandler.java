package com.virjar.vscrawler.core.selector.xpath.parse.expression.token.handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.nodes.Element;

import com.virjar.vscrawler.core.selector.xpath.parse.expression.SyntaxNode;
import com.virjar.vscrawler.core.selector.xpath.parse.expression.token.Token;
import com.virjar.vscrawler.core.selector.xpath.parse.expression.token.TokenHandler;

/**
 * Created by virjar on 17/6/12.
 */
public class NumberHandler implements TokenHandler {
    @Override
    public SyntaxNode parseToken(final String tokenStr) {
        return new SyntaxNode() {
            @Override
            public Object calc(Element element) {
                if (StringUtils.contains(tokenStr, ".")) {
                    return NumberUtils.toDouble(tokenStr);
                } else {
                    return NumberUtils.toInt(tokenStr);
                }
            }

            @Override
            public Class judeResultType() {
                return Double.class;
            }
        };
    }

    @Override
    public String typeName() {
        return Token.NUMBER;
    }
}
