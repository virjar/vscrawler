package com.virjar.vscrawler.core.selector.string.function.commonlang3;

import com.virjar.vscrawler.core.selector.string.function.ISSZeroFunction;
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
    protected int handleIndex(CharSequence str, CharSequence searchStr, int startPos) {
        return StringUtils.indexOf(str, searchStr, startPos);
    }
}
