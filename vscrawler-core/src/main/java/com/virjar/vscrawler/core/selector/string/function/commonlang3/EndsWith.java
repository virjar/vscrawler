package com.virjar.vscrawler.core.selector.string.function.commonlang3;

import com.virjar.vscrawler.core.selector.string.function.StartEndFunction;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by virjar on 17/7/8.
 */
public class EndsWith extends StartEndFunction {
    @Override
    protected boolean handle(String input, String searchString, boolean ignoreCase) {
        if (ignoreCase) {
            return StringUtils.endsWithIgnoreCase(input, searchString);
        } else {
            return StringUtils.endsWith(input, searchString);
        }
    }

    @Override
    public String determineFunctionName() {
        return "endsWith";
    }
}
