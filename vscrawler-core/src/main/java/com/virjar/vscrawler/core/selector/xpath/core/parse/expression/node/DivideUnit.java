package com.virjar.vscrawler.core.selector.xpath.core.parse.expression.node;

import com.virjar.vscrawler.core.selector.xpath.model.JXNode;

/**
 * Created by virjar on 17/6/10.
 *
 *
 * @author virjar
 * @since 0.0.1 除法运算
 */
@OpKey("/")
public class DivideUnit extends AlgorithmUnit {

    @Override
    public Object calc(JXNode jxNode) {
        Object leftValue = left.calc(jxNode);
        Object rightValue = right.calc(jxNode);
        if (leftValue == null || rightValue == null) {
            return null;
        }
        // 左右都不为空,开始计算
        // step one think as number
        if (leftValue instanceof Number && rightValue instanceof Number) {
            // TODO 除数为0如何处理
            return ((Number) leftValue).doubleValue() / ((Number) rightValue).doubleValue();
        }

        return null;
    }

    @Override
    public Class judeResultType() {
        return null;
    }
}
