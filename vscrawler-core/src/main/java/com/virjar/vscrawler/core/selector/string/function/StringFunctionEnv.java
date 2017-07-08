package com.virjar.vscrawler.core.selector.string.function;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Created by virjar on 17/7/1.
 */
public class StringFunctionEnv {
    private static Map<String, StringFunction> allFunction = Maps.newHashMap();

    static {
        registerDefault();
    }

    private static void registerDefault() {
        register(new IndexOf());
        register(new IndexOfIgnoreCase());
        register(new LastIndexOf());
        register(new LastIndexOfIgnoreCase());
        register(new Regex());
        register(new SubString());
        register(new SubstringsBetween());
        register(new Trim());
        register(new TrimToEmpty());
        register(new Truncate());
    }

    public static void register(StringFunction stringFunction) {
        allFunction.put(stringFunction.determineFunctionName(), stringFunction);
    }

    public static StringFunction findFunction(String functionName) {
        return allFunction.get(functionName);
    }
}
