package com.virjar.vscrawler.core.selector.xpath.core.parse.expression.node;

import com.virjar.vscrawler.core.selector.xpath.model.JXNode;

/**
 * Created by virjar on 17/6/10.
 */
@OpKey("=")
public class EqualUnit extends AlgorithmUnit {
    @Override
    public Object calc(JXNode jxNode) {
        Object leftValue = left.calc(jxNode);
        Object rightValue = right.calc(jxNode);
        if (leftValue == null && rightValue == null) {
            return Boolean.TRUE;
        }
        if (leftValue == null || rightValue == null) {
            return Boolean.FALSE;
        }

        return leftValue.equals(rightValue);
    }

    @Override
    public Class judeResultType() {
        return null;
    }
}
