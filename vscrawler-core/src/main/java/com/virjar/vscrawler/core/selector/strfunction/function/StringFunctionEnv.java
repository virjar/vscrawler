package com.virjar.vscrawler.core.selector.strfunction.function;

import java.util.Map;

import com.google.common.collect.Maps;
/**
 * Created by virjar on 17/7/1.
 */
public class StringFunctionEnv {
    private static Map<String, StringFunction> allFunction = Maps.newHashMap();

    public static StringFunction findFunction(String functionName) {
        return allFunction.get(functionName);
    }
}
