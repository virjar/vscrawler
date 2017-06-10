package com.virjar.vscrawler.core.selector.xpath.core.parse.expression.node;

import com.virjar.vscrawler.core.selector.xpath.model.JXNode;

/**
 * Created by virjar on 17/6/10.
 */
public abstract class BooleanRevertUnit extends WrapperUnit {
    @Override
    public Object calc(JXNode jxNode) {
        return !((Boolean) wrap().calc(jxNode));
    }

    @Override
    public Class judeResultType() {
        return Boolean.class;
    }
}
