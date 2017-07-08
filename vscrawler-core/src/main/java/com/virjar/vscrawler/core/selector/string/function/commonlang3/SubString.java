package com.virjar.vscrawler.core.selector.string.function.commonlang3;

import com.virjar.vscrawler.core.selector.string.function.SSZeroLengthFunction;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by virjar on 17/7/8.
 */
public class SubString extends SSZeroLengthFunction {
    @Override
    protected String handle(String str, int start, int end) {
        return StringUtils.substring(str, start, end);
    }

    @Override
    public String determineFunctionName() {
        return "substring";
    }
}
