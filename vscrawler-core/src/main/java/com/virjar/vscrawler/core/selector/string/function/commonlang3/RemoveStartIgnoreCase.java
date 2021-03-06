package com.virjar.vscrawler.core.selector.string.function.commonlang3;

import com.virjar.vscrawler.core.selector.string.function.SSSFunctionSingle;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by virjar on 17/7/8.
 */
public class RemoveStartIgnoreCase extends SSSFunctionSingle {
    @Override
    protected String handle(String input, String second) {
        return StringUtils.removeStartIgnoreCase(input, second);
    }

    @Override
    public String determineFunctionName() {
        return "removeStartIgnoreCase";
    }
}
