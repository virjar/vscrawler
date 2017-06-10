package com.virjar.vscrawler.core.selector.xpath.core.parse.expression.node;

import com.virjar.vscrawler.core.selector.xpath.model.JXNode;

/**
 * Created by virjar on 17/6/10.
 */
@OpKey("||")
public class OrUnit extends AlgorithmUnit {
    @Override
    public Object calc(JXNode jxNode) {
        Object leftValue = left.calc(jxNode);
        Object rightValue = right.calc(jxNode);
        // 左边为true,右边不管是啥,都为真
        if (leftValue != null && leftValue instanceof Boolean && (Boolean) leftValue) {
            return true;
        }

        // 左边不为真,以右边为主
        if (rightValue != null && rightValue instanceof Boolean && (Boolean) rightValue) {
            return true;
        }
        return Boolean.FALSE;
    }

    @Override
    public Class judeResultType() {
        return Boolean.class;
    }
}
