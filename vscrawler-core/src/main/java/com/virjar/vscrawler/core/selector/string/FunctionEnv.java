package com.virjar.vscrawler.core.selector.string;

import java.util.Map;

import com.google.common.collect.Maps;
import com.virjar.vscrawler.core.selector.string.tree.FunctionNode;

/**
 * Created by virjar on 17/7/1.
 */
public class FunctionEnv {
    private static Map<String, FunctionNode> allFunction = Maps.newHashMap();

    public static FunctionNode findFunction(String functionName) {
        return allFunction.get(functionName);
    }
}
