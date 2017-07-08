package com.virjar.vscrawler.core.selector.string.function;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.selector.string.Strings;
import com.virjar.vscrawler.core.selector.string.syntax.StringContext;
import com.virjar.vscrawler.core.selector.string.syntax.SyntaxNode;

/**
 * Created by virjar on 17/7/8.
 */
public abstract class AbstractSplitFunction extends FirstStringsFunction {
    @Override
    Strings handle(Strings input, StringContext stringContext, List<SyntaxNode> params) {
        String sepatatorChars = null;
        int max = -1;
        boolean preserveAllTokens = false;
        int index = 1;
        LinkedList<Object> calcData = Lists.newLinkedList();
        if (params.size() > index + 1) {
            Object calculate = params.get(index).calculate(stringContext);
            if (calculate instanceof CharSequence) {
                sepatatorChars = calculate.toString();
                index++;
            } else {
                calcData.add(calculate);
            }
        }

        if (!calcData.isEmpty() && calcData.peek() instanceof Number) {
            max = ((Number) calcData.removeFirst()).intValue();
        } else if (params.size() > index + 1) {
            Object calculate = params.get(index).calculate(stringContext);
            if (calculate instanceof Number) {
                max = ((Number) calculate).intValue();
                index++;
            } else {
                calcData.add(calculate);
            }
        }

        if (!calcData.isEmpty() && calcData.peek() instanceof Boolean) {
            preserveAllTokens = (Boolean) calcData.removeFirst();
        } else if (params.size() > index + 1) {
            Object calculate = params.get(index).calculate(stringContext);
            if (calculate instanceof Boolean) {
                preserveAllTokens = (boolean) calculate;
            }
        }

        Strings ret = new Strings();
        for (String str : input) {
            Collections.addAll(ret, split(str, sepatatorChars, max, preserveAllTokens));

        }
        return ret;
    }

    abstract String[] split(String str, String separatorChars, int max, boolean preserveAllTokens);
}
