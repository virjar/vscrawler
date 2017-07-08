package com.virjar.vscrawler.core.selector.string.function.commonlang3;

import com.virjar.vscrawler.core.selector.string.function.ISFunction;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by virjar on 17/7/8.
 */
public class Length extends ISFunction {

    @Override
    public String determineFunctionName() {
        return "length";
    }

    @Override
    protected Integer handle(String input) {
        return StringUtils.length(input);
    }
}
