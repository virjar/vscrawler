package com.virjar.vscrawler.core.selector.string.function;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by virjar on 17/7/8.
 */
public class IndexOfIgnoreCase extends ISSZeroFunction {

    @Override
    int handleIndex(CharSequence str, CharSequence searchStr, int startPos) {
        return StringUtils.indexOfIgnoreCase(str, searchStr, startPos);
    }

    @Override
    public String determineFunctionName() {
        return "indexOfIgnoreCase";
    }
}
