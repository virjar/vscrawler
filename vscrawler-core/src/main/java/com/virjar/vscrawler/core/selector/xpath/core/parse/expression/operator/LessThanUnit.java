package com.virjar.vscrawler.core.selector.xpath.core.parse.expression.operator;

import org.jsoup.nodes.Element;

import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.node.AlgorithmUnit;
import com.virjar.vscrawler.core.selector.xpath.util.XpathUtil;

/**
 * Created by virjar on 17/6/10.
 */
@OpKey(value = "<", priority = 10)
public class LessThanUnit extends AlgorithmUnit {
    @Override
    public Object calc(Element element) {
        Object leftValue = left.calc(element);
        Object rightValue = right.calc(element);
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
