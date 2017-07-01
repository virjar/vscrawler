package com.virjar.vscrawler.core.selector.string;

import java.util.LinkedHashSet;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.virjar.vscrawler.core.selector.string.tree.FunctionType;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by virjar on 17/7/1.
 */
@Slf4j
public class StingEvaluator {
    private FunctionType functionType;

    public StingEvaluator(FunctionType functionType) {
        this.functionType = functionType;
    }

    public List<String> evaluate(List<String> input) {
        LinkedHashSet<String> linkedHashSet = Sets.newLinkedHashSet();
        for (String str : input) {
            linkedHashSet.addAll(functionType.getStringFunction().call(str, functionType.getParams()));
        }
        return Lists.newLinkedList(linkedHashSet);
    }
}
