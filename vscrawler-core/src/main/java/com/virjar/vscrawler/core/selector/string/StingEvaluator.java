package com.virjar.vscrawler.core.selector.string;

import java.util.LinkedHashSet;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.virjar.vscrawler.core.selector.string.syntax.FunctionSyntaxNode;
import com.virjar.vscrawler.core.selector.string.syntax.StringContext;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by virjar on 17/7/1.
 */
@Slf4j
public class StingEvaluator {
    private FunctionSyntaxNode stringFunction;

    public StingEvaluator(FunctionSyntaxNode functionType) {
        this.stringFunction = functionType;
    }

    public List<String> evaluate(List<String> input, String baseUrl) {
        LinkedHashSet<String> linkedHashSet = Sets.newLinkedHashSet();
        int i = 0;
        for (String str : input) {
            StringContext stringContext = new StringContext(baseUrl, str, input, i);
            Object calculate = stringFunction.calculate(stringContext);
            if (!(calculate instanceof Strings)) {
                log.warn("result type for function: " + stringFunction.functionName() + " is not strings");
            } else {
                linkedHashSet.addAll((Strings) calculate);
            }
            i++;
        }
        return Lists.newLinkedList(linkedHashSet);
    }
}
