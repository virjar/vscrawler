package com.virjar.vscrawler.core.selector.xpath.parse.expression.operator;

import com.virjar.vscrawler.core.selector.xpath.parse.expression.node.AlgorithmUnit;
import org.jsoup.nodes.Element;

/**
 * Created by virjar on 17/6/10.
 */
@OpKey(value = "||", priority = 0)
public class OrUnit extends AlgorithmUnit {
    @Override
    public Object calc(Element element) {
        Object leftValue = left.calc(element);
        Object rightValue = right.calc(element);
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
