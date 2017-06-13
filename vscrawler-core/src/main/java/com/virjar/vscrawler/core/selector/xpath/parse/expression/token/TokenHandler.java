package com.virjar.vscrawler.core.selector.xpath.parse.expression.token;

import com.virjar.vscrawler.core.selector.xpath.parse.expression.SyntaxNode;
import com.virjar.vscrawler.core.selector.xpath.exception.XpathSyntaxErrorException;

/**
 * Created by virjar on 17/6/12.
 */
public interface TokenHandler {
    SyntaxNode parseToken(String tokenStr) throws XpathSyntaxErrorException;

    String typeName();
}
