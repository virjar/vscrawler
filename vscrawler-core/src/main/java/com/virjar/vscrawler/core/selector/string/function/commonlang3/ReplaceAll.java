package com.virjar.vscrawler.core.selector.string.function.commonlang3;

import com.virjar.vscrawler.core.selector.string.function.SSSSFunctionSingle;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by virjar on 17/7/8.
 */
public class ReplaceAll extends SSSSFunctionSingle {
    @Override
    protected String handle(String input, String second, String third) {
        return StringUtils.replaceAll(input, second, third);
    }

    @Override
    public String determineFunctionName() {
        return "replaceAll";
    }
}
