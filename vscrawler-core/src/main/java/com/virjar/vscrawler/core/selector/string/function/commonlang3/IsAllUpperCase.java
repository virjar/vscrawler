package com.virjar.vscrawler.core.selector.string.function.commonlang3;

import com.virjar.vscrawler.core.selector.string.function.BSFunction;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by virjar on 17/7/8.
 */
public class IsAllUpperCase extends BSFunction {
    @Override
    protected boolean handle(CharSequence str) {
        return StringUtils.isAllUpperCase(str);
    }

    @Override
    public String determineFunctionName() {
        return "isAllUpperCase";
    }
}
