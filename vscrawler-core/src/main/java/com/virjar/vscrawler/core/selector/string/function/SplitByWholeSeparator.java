package com.virjar.vscrawler.core.selector.string.function;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by virjar on 17/7/8.
 */
public class SplitByWholeSeparator extends AbstractSplitFunction {
    @Override
    String[] split(String str, String separatorChars, int max, boolean preserveAllTokens) {
        if (preserveAllTokens) {
            return StringUtils.splitByWholeSeparatorPreserveAllTokens(str, separatorChars, max);
        } else {
            return StringUtils.splitByWholeSeparator(str, separatorChars, max);
        }
    }

    @Override
    public String determineFunctionName() {
        return "splitByWholeSeparator";
    }
}
