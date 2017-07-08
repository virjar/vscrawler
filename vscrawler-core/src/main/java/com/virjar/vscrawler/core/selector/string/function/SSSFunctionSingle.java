package com.virjar.vscrawler.core.selector.string.function;

import java.util.List;

import com.google.common.base.Preconditions;
import com.virjar.vscrawler.core.selector.string.Strings;
import com.virjar.vscrawler.core.selector.string.syntax.StringContext;
import com.virjar.vscrawler.core.selector.string.syntax.SyntaxNode;

/**
 * Created by virjar on 17/7/8.
 */
public abstract class SSSFunctionSingle extends FirstStringsFunction {
    @Override
    Strings handle(Strings input, StringContext stringContext, List<SyntaxNode> params) {
        Preconditions.checkArgument(params.size() >= 2, determineFunctionName() + " must have 2 parameters at last");

        Object secondObject = params.get(1).calculate(stringContext);
        if (!(secondObject instanceof CharSequence)) {
            throw new IllegalStateException(
                    determineFunctionName() + " second parameter must be string,now is : " + secondObject);
        }
        Strings ret = new Strings();
        String secondStr = secondObject.toString();
        for (String str : input) {
            ret.add(handle(str, secondStr));
        }
        return ret;
    }

    abstract String handle(String input, String second);
}
