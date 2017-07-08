package com.virjar.vscrawler.core.selector.string.function;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by virjar on 17/7/8.
 */
public class LastIndexOf extends ISSZeroFunction {

    @Override
    public String determineFunctionName() {
        return "lastIndexOf";
    }

    @Override
    int handleIndex(CharSequence str, CharSequence searchStr, int startPos) {
        return StringUtils.lastIndexOf(str, searchStr, startPos);
    }
}
