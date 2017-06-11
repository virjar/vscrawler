package com.virjar.vscrawler.core.selector.xpath.core.parse.expression.operator;

import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.node.BooleanRevertUnit;

/**
 * Created by virjar on 17/6/10.
 */
@OpKey(value = ">=", priority = 10)
public class GreaterEqualUnit extends BooleanRevertUnit {
    @Override
    protected String targetName() {
        return "<";
    }
}
