package com.virjar.vscrawler.core.selector.string;

import java.util.LinkedHashSet;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.virjar.vscrawler.core.selector.string.tree.FunctionNode;
import com.virjar.vscrawler.core.selector.string.tree.StringFunctionResult;
import com.virjar.vscrawler.core.selector.string.tree.StringResult;
import com.virjar.vscrawler.core.selector.string.tree.StringsResult;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by virjar on 17/7/1.
 */
@Slf4j
public class StingEvaluator {
    private FunctionNode functionNode;
    private List<FunctionNode> params;

    public StingEvaluator(FunctionNode functionNode, List<FunctionNode> params) {
        this.functionNode = functionNode;
        this.params = params;
    }

    public List<String> evaluate(List<String> input) {
        LinkedHashSet<String> linkedHashSet = Sets.newLinkedHashSet();
        for (String str : input) {
            StringFunctionResult result = functionNode.call(str, params);
            if (result instanceof StringsResult) {
                linkedHashSet.addAll(((StringsResult) result).getValue());
            } else if (result instanceof StringResult) {
                linkedHashSet.add(((StringResult) result).getValue());
            } else {
                log.warn("can not convert string function call result:" + result);
            }
        }
        return Lists.newLinkedList(linkedHashSet);
    }
}
