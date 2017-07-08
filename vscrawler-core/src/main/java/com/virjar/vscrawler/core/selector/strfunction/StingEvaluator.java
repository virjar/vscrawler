package com.virjar.vscrawler.core.selector.strfunction;

import java.util.LinkedHashSet;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.virjar.vscrawler.core.selector.strfunction.syntax.FunctionSyntaxNode;
import com.virjar.vscrawler.core.selector.strfunction.syntax.StringContext;

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
            linkedHashSet.addAll(stringFunction.call(stringContext));
            i++;
        }
        return Lists.newLinkedList(linkedHashSet);
    }
}
