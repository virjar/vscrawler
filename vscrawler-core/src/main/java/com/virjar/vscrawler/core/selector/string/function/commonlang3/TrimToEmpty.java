package com.virjar.vscrawler.core.selector.string.function.commonlang3;

import com.virjar.vscrawler.core.selector.string.function.SSFunction;
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
    protected String handleSingleStr(String input) {
        return StringUtils.trimToEmpty(input);
    }
}
