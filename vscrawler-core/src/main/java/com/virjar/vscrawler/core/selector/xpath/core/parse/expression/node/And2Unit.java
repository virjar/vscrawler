package com.virjar.vscrawler.core.selector.xpath.core.parse.expression.node;

import com.virjar.vscrawler.core.selector.xpath.model.JXNode;

/**
 * Created by virjar on 17/6/10.
 */
@OpKey("and")
public class And2Unit extends WrapperUnit {
    @Override
    String targetName() {
        return "&&";
    }

    @Override
    public Object calc(JXNode jxNode) {
        return wrap().calc(jxNode);
    }

    @Override
    public Class judeResultType() {
        return wrap().judeResultType();
    }
}
