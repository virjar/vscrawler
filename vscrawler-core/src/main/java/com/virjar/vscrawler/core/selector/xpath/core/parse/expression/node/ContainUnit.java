package com.virjar.vscrawler.core.selector.xpath.core.parse.expression.node;

import com.virjar.vscrawler.core.selector.xpath.model.JXNode;
import com.virjar.vscrawler.core.selector.xpath.util.XpathUtil;

/**
 * Created by virjar on 17/6/10.
 */
@OpKey("*=")
public class ContainUnit extends AlgorithmUnit {
    @Override
    public Object calc(JXNode jxNode) {
        Object leftValue = left.calc(jxNode);
        Object rightValue = right.calc(jxNode);
        if (leftValue == null || rightValue == null) {
            return Boolean.FALSE;
        }
        return XpathUtil.toPlainString(leftValue).contains(XpathUtil.toPlainString(rightValue));
    }

    @Override
    public Class judeResultType() {
        return Boolean.class;
    }
}
