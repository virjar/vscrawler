package com.virjar.vscrawler.core.selector.string.function.commonlang3;

import org.apache.commons.lang3.StringUtils;

import com.virjar.vscrawler.core.selector.string.function.SSFunction;

/**
 * Created by virjar on 17/7/8.
 */
public class Capitalize extends SSFunction {
    @Override
    protected String handleSingleStr(String input) {
        return StringUtils.capitalize(input);
    }

    @Override
    public String determineFunctionName() {
        return "capitalize";
    }
}
