package com.virjar.vscrawler.core.selector.string.function;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by virjar on 17/7/8.
 */
public class DeleteWhitespace extends SSFunction {

    @Override
    String handleSingleStr(String input) {
        return StringUtils.deleteWhitespace(input);
    }

    @Override
    public String determineFunctionName() {
        return "deleteWhitespace";
    }
}
