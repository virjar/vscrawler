package com.virjar.vscrawler.core.selector.xpath.core.parse.expression.node;

import com.virjar.vscrawler.core.selector.xpath.model.JXNode;
import com.virjar.vscrawler.core.selector.xpath.util.XpathUtil;

/**
 * Created by virjar on 17/6/10.
 */
@OpKey("<")
public class LessThanUnit extends AlgorithmUnit {
    @Override
    public Object calc(JXNode jxNode) {
        Object leftValue = left.calc(jxNode);
        Object rightValue = right.calc(jxNode);
        if (leftValue == null || rightValue == null) {
            return XpathUtil.toPlainString(rightValue).compareTo(XpathUtil.toPlainString(rightValue)) < 0;
        }
        // 左右都不为空,开始计算
        // step one think as number
        if (leftValue instanceof Number && rightValue instanceof Number) {
            return ((Number) leftValue).doubleValue() < ((Number) rightValue).doubleValue();
        }

        return XpathUtil.toPlainString(leftValue).compareTo(XpathUtil.toPlainString(rightValue)) < 0;
    }

    @Override
    public Class judeResultType() {
        return null;
    }
}
