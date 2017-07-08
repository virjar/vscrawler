package com.virjar.vscrawler.core.selector.string.function.commonlang3;

import com.virjar.vscrawler.core.selector.string.function.ISSZeroFunction;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by virjar on 17/7/8.
 */
public class LastIndexOfIgnoreCase extends ISSZeroFunction {
    @Override
    protected  int handleIndex(CharSequence str, CharSequence searchStr, int startPos) {
        return StringUtils.lastIndexOfIgnoreCase(str, searchStr, startPos);
    }

    @Override
    public String determineFunctionName() {
        return "lastIndexOfIgnoreCase";
    }
}
