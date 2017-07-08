package com.virjar.vscrawler.core.selector.string.function;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by virjar on 17/7/8.
 */
public class SubString extends SSZeroLengthFunction {
    @Override
    String handle(String str, int start, int end) {
        return StringUtils.substring(str, start, end);
    }

    @Override
    public String determineFunctionName() {
        return "substring";
    }
}
