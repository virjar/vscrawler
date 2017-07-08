package com.virjar.vscrawler.core.selector.string.function;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by virjar on 17/7/8.
 */
public class TrimToEmpty extends SSFunction {

    @Override
    public String determineFunctionName() {
        return "trimToEmpty";
    }

    @Override
    String handleSingleStr(String input) {
        return StringUtils.trimToEmpty(input);
    }
}
