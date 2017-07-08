package com.virjar.vscrawler.core.selector.string.function;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.selector.string.Strings;
import com.virjar.vscrawler.core.selector.string.syntax.StringContext;
import com.virjar.vscrawler.core.selector.string.syntax.SyntaxNode;

/**
 * Created by virjar on 17/7/8.
 */
public class Join extends FirstStringsFunction {
    @Override
    Strings handle(Strings input, StringContext stringContext, List<SyntaxNode> params) {
        String separator = null;
        Integer startIndex = 0;
        Integer endIndex = input.size();
        int index = 1;
        LinkedList<Object> calcData = Lists.newLinkedList();
        if (params.size() > 2) {
            Object calculate = params.get(index).calculate(stringContext);
            if (calculate instanceof CharSequence) {
                separator = calculate.toString();
                index++;
            } else {
                calcData.add(calculate);
            }
        }

        if (!calcData.isEmpty() && calcData.peek() instanceof Number) {
            startIndex = ((Number) calcData.removeFirst()).intValue();
        } else if (params.size() > index + 1) {
            Object calculate = params.get(index).calculate(stringContext);
            if (calculate instanceof Number) {
                startIndex = ((Number) calculate).intValue();
                index++;
            } else {
                calcData.add(calculate);
            }
        }

        if (!calcData.isEmpty() && calcData.peek() instanceof Number) {
            endIndex = ((Number) calcData.removeFirst()).intValue();
        } else if (params.size() > index + 1) {
            Object calculate = params.get(index).calculate(stringContext);
            if (calculate instanceof Number) {
                endIndex = ((Number) calculate).intValue();
            }
        }

        return new Strings(StringUtils.join(input.toArray(), separator, startIndex, endIndex));
    }

    @Override
    public String determineFunctionName() {
        return "join";
    }
}
