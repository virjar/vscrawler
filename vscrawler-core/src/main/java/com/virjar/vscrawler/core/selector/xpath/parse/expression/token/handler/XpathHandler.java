package com.virjar.vscrawler.core.selector.xpath.parse.expression.token.handler;

import com.virjar.vscrawler.core.selector.xpath.parse.XpathParser;
import com.virjar.vscrawler.core.selector.xpath.parse.expression.SyntaxNode;
import com.virjar.vscrawler.core.selector.xpath.parse.expression.token.Token;
import com.virjar.vscrawler.core.selector.xpath.parse.expression.token.TokenHandler;
import org.jsoup.nodes.Element;

import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.selector.xpath.exception.XpathSyntaxErrorException;
import com.virjar.vscrawler.core.selector.xpath.model.JXNode;
import com.virjar.vscrawler.core.selector.xpath.model.XpathEvaluator;

/**
 * Created by virjar on 17/6/12.
 */
public class XpathHandler implements TokenHandler {
    @Override
    public SyntaxNode parseToken(String tokenStr) throws XpathSyntaxErrorException {
        final XpathEvaluator xpathEvaluator = new XpathParser(tokenStr).parse();
        return new SyntaxNode() {
            @Override
            public Object calc(Element element) {
                return xpathEvaluator.evaluate(Lists.newArrayList(JXNode.e(element)));
            }

            @Override
            public Class judeResultType() {
                return String.class;
            }
        };
    }

    @Override
    public String typeName() {
        return Token.XPATH;
    }
}
