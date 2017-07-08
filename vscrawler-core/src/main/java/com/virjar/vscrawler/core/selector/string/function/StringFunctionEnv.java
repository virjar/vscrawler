package com.virjar.vscrawler.core.selector.string.function;

import java.util.Map;

import com.google.common.collect.Maps;
import com.virjar.vscrawler.core.selector.string.function.commonlang3.*;
import com.virjar.vscrawler.core.selector.string.function.vs.Regex;
import com.virjar.vscrawler.core.selector.string.function.vs.RuntimeString;
import com.virjar.vscrawler.core.selector.string.function.vs.Test;

/**
 * Created by virjar on 17/7/1.
 */
public class StringFunctionEnv {
    public static void main(String[] args) {
        System.out.println(allFunction.size());
    }

    private static Map<String, StringFunction> allFunction = Maps.newHashMap();

    static {
        registerCommonLang3();
        registerVS();
    }

    private static void registerVS() {
        register(new Regex());
        register(new RuntimeString());
        register(new Test());
    }

    /**
     * 自common-lang3迁移过来的函数
     */
    private static void registerCommonLang3() {
        register(new IndexOf());
        register(new IndexOfIgnoreCase());
        register(new LastIndexOf());
        register(new LastIndexOfIgnoreCase());
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
        register(new Length());
        register(new UpperCase());
        register(new LowerCase());
        register(new Capitalize());
        register(new Uncapitalize());
        register(new SwapCase());
        register(new IsAlpha());
        register(new IsAlphaSpace());
        register(new IsAlphanumeric());
        register(new IsAlphanumericSpace());
        register(new IsAsciiPrintable());
        register(new IsNumeric());
        register(new IsNumericSpace());
        register(new IsAllLowerCase());
        register(new IsAllUpperCase());
        register(new Reverse());
        register(new StartsWith());
        register(new EndsWith());
    }

    public static void register(StringFunction stringFunction) {
        allFunction.put(stringFunction.determineFunctionName(), stringFunction);
    }

    public static StringFunction findFunction(String functionName) {
        return allFunction.get(functionName);
    }
}
