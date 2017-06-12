package com.virjar.vscrawler.core.selector.xpath.core.parse.expression.operator;

import org.jsoup.nodes.Element;

import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.node.AlgorithmUnit;
import com.virjar.vscrawler.core.selector.xpath.util.XpathUtil;

/**
 * Created by virjar on 17/6/10.
 * 
 * @author virjar
 * @since 0.0.1 加法运算
 */
@OpKey(value = "+", priority = 20)
public class AddUnit extends AlgorithmUnit {

    @Override
    public Object calc(Element element) {
        Object leftValue = left.calc(element);
        Object rightValue = right.calc(element);
        if (leftValue == null || rightValue == null) {
            return XpathUtil.toPlainString(leftValue) + XpathUtil.toPlainString(rightValue);
        }
        // 左右都不为空,开始计算
        // step one think as number
        if (leftValue instanceof Number && rightValue instanceof Number) {
            if (leftValue instanceof Integer && rightValue instanceof Integer) {
                return (Integer) leftValue + (Integer) rightValue;
            }
            return ((Number) leftValue).doubleValue() + ((Number) rightValue).doubleValue();
        }

        return XpathUtil.toPlainString(leftValue) + XpathUtil.toPlainString(rightValue);
    }

    @Override
    public Class judeResultType() {
        return null;
    }
}
