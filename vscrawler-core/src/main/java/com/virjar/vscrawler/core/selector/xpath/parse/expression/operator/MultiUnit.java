package com.virjar.vscrawler.core.selector.xpath.parse.expression.operator;

import org.jsoup.nodes.Element;

import com.virjar.vscrawler.core.selector.xpath.parse.expression.node.AlgorithmUnit;

/**
 * Created by virjar on 17/6/10.
 *
 * @author virjar
 * @since 0.0.1 乘法运算
 */
@OpKey(value = "*", priority = 30)
public class MultiUnit extends AlgorithmUnit {

    @Override
    public Object calc(Element element) {
        Object leftValue = left.calc(element);
        Object rightValue = right.calc(element);
        if (leftValue == null || rightValue == null) {
            return null;
        }
        // 左右都不为空,开始计算
        // step one think as number
        if (leftValue instanceof Number && rightValue instanceof Number) {
            return ((Number) leftValue).doubleValue() * ((Number) rightValue).doubleValue();
        }

        return null;
    }

    @Override
    public Class judeResultType() {
        return null;
    }
}
