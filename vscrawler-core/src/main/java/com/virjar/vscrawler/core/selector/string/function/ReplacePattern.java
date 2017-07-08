package com.virjar.vscrawler.core.selector.string.function;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by virjar on 17/7/8.
 */
public class ReplacePattern extends SSSSFunctionSingle {
    @Override
    String handle(String input, String second, String third) {
        return StringUtils.replacePattern(input, second, third);
    }

    @Override
    public String determineFunctionName() {
        return "replacePattern";
    }
}
