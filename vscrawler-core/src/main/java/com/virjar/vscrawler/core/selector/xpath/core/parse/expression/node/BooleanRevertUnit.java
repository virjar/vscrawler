package com.virjar.vscrawler.core.selector.xpath.core.parse.expression.node;

import org.jsoup.nodes.Element;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by virjar on 17/6/10.
 */
@Slf4j
public abstract class BooleanRevertUnit extends WrapperUnit {
    @Override
    public Object calc(Element element) {
        return !((Boolean) wrap().calc(element));
    }

    @Override
    public Class judeResultType() {
        return Boolean.class;
    }
}
