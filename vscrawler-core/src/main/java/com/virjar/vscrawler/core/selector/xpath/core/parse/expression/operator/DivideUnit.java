package com.virjar.vscrawler.core.selector.xpath.core.parse.expression.operator;

import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.node.AlgorithmUnit;
import com.virjar.vscrawler.core.selector.xpath.model.JXNode;
import org.jsoup.nodes.Element;

/**
 * Created by virjar on 17/6/10.
 *
 *
 * @author virjar
 * @since 0.0.1 除法运算
 */
@OpKey(value = "/", priority = 30)
public class DivideUnit extends AlgorithmUnit {

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
