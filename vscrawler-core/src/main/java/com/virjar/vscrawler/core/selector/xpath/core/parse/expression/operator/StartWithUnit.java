package com.virjar.vscrawler.core.selector.xpath.core.parse.expression.operator;

import org.jsoup.nodes.Element;

import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.node.AlgorithmUnit;
import com.virjar.vscrawler.core.selector.xpath.util.XpathUtil;

/**
 * Created by virjar on 17/6/10.
 */
@OpKey(value = "^=", priority = 10)
public class StartWithUnit extends AlgorithmUnit {
    @Override
    public Object calc(Element element) {
        Object leftValue = left.calc(element);
        Object rightValue = right.calc(element);
        if (leftValue == null || rightValue == null) {
            return Boolean.FALSE;
        }
        return XpathUtil.toPlainString(leftValue).startsWith(XpathUtil.toPlainString(rightValue));
    }

    @Override
    public Class judeResultType() {
        return Boolean.class;
    }
}
