package com.virjar.vscrawler.core.selector.string.function.commonlang3;

import com.virjar.vscrawler.core.selector.string.function.BSFunction;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by virjar on 17/7/8.
 */
public class IsAsciiPrintable extends BSFunction {
    @Override
    protected boolean handle(CharSequence str) {
        return StringUtils.isAsciiPrintable(str);
    }

    @Override
    public String determineFunctionName() {
        return "isAsciiPrintable";
    }
}
