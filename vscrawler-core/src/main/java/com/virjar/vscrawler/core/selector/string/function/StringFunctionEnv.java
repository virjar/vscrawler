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
        register(new Split());
        register(new SplitByWholeSeparator());
        register(new Join());
        register(new DeleteWhitespace());
        register(new RemoveStart());
        register(new RemoveStartIgnoreCase());
        register(new RemoveEnd());
        register(new RemoveEndIgnoreCase());
        register(new Remove());
        register(new RemoveIgnoreCase());
        register(new RemoveAll());
        register(new RemoveFirst());
        register(new ReplaceOnce());
        register(new ReplaceOnceIgnoreCase());
        register(new ReplacePattern());
        register(new RemovePattern());
        register(new ReplaceAll());
        register(new ReplaceFirst());
    }

    public static void register(StringFunction stringFunction) {
        allFunction.put(stringFunction.determineFunctionName(), stringFunction);
    }

    public static StringFunction findFunction(String functionName) {
        return allFunction.get(functionName);
    }
}
