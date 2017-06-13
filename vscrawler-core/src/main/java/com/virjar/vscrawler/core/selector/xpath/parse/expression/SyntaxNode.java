package com.virjar.vscrawler.core.selector.xpath.parse.expression;

import org.jsoup.nodes.Element;

/**
 * Created by virjar on 17/6/10. 语法节点树
 */
public interface SyntaxNode {
    Object calc(Element element);

    /**
     * 用于类型推测,对于确定的表达式,其返回值类型应该是确定的
     * 
     * @return Type
     */
    Class judeResultType();
}
