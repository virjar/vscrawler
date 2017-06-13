package com.virjar.vscrawler.core.selector.xpath.parse.expression.operator;

import com.virjar.vscrawler.core.selector.xpath.parse.expression.node.AlgorithmUnit;
import org.jsoup.nodes.Element;

/**
 * Created by virjar on 17/6/10.
 */
@OpKey(value = "=", priority = 10)
public class EqualUnit extends AlgorithmUnit {
    @Override
    public Object calc(Element element) {
        Object leftValue = left.calc(element);
        Object rightValue = right.calc(element);
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
