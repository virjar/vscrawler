package com.virjar.vscrawler.core.selector.string.function;

import java.util.List;

import com.google.common.base.Preconditions;
import com.virjar.vscrawler.core.selector.string.Strings;
import com.virjar.vscrawler.core.selector.string.syntax.StringContext;
import com.virjar.vscrawler.core.selector.string.syntax.SyntaxNode;

/**
 * Created by virjar on 17/7/8.
 */
public abstract class ISSZeroFunction implements StringFunction {
    @Override
    public Object call(StringContext stringContext, List<SyntaxNode> params) {
        Preconditions.checkArgument(params.size() >= 2, determineFunctionName() + ":函数至少包含2个参数");

        Object string = params.get(0).calculate(stringContext);
        if (string == null) {
            return new Strings();
        }
        Object subString = params.get(1).calculate(stringContext);
        if (subString == null) {
            throw new IllegalStateException("subString for "+determineFunctionName()+" cannot be null");
        }

        if (!(subString instanceof String)) {
            throw new IllegalStateException("second params must be string ");
        }

        int searchStart = 0;
        if (params.size() >= 3) {
            Object indexObject = params.get(2).calculate(stringContext);
            if (!(indexObject instanceof Number)) {
                throw new IllegalStateException(
                        determineFunctionName() + " function ,third parameter must be number ,now is :" + indexObject);
            }
            searchStart = ((Number) indexObject).intValue();
        }
        return handleIndex(string.toString(), subString.toString(), searchStart);
    }

    abstract int handleIndex(final CharSequence str, final CharSequence searchStr, int startPos);
}
