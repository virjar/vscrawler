package com.virjar.vscrawler.core.selector.xpath.parse.expression.operator;

import org.jsoup.nodes.Element;

import com.virjar.vscrawler.core.selector.xpath.parse.expression.node.WrapperUnit;

/**
 * Created by virjar on 17/6/10.
 */
@OpKey(value = "and", priority = 0)
public class And2Unit extends WrapperUnit {
    @Override
    protected String targetName() {
        return "&&";
    }

    @Override
    public Object calc(Element element) {
        return wrap().calc(element);
    }

    @Override
    public Class judeResultType() {
        return wrap().judeResultType();
    }
}
