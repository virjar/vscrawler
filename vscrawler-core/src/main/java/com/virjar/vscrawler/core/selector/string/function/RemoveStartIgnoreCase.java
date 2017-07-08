package com.virjar.vscrawler.core.selector.string.function;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by virjar on 17/7/8.
 */
public class RemoveStartIgnoreCase extends SSSFunctionSingle {
    @Override
    String handle(String input, String second) {
        return StringUtils.removeStartIgnoreCase(input, second);
    }

    @Override
    public String determineFunctionName() {
        return "removeStartIgnoreCase";
    }
}
