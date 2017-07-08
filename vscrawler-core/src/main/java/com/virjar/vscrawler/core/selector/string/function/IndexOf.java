package com.virjar.vscrawler.core.selector.string.function;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by virjar on 17/7/8.
 */
public class IndexOf extends ISSZeroFunction {

    @Override
    public String determineFunctionName() {
        return "indexOf";
    }

    @Override
    int handleIndex(CharSequence str, CharSequence searchStr, int startPos) {
        return StringUtils.indexOf(str, searchStr, startPos);
    }
}
