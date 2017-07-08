package com.virjar.vscrawler.core.selector.string.function;

import java.util.List;

import com.google.common.base.Preconditions;
import com.virjar.vscrawler.core.selector.string.Strings;
import com.virjar.vscrawler.core.selector.string.syntax.StringContext;
import com.virjar.vscrawler.core.selector.string.syntax.SyntaxNode;

/**
 * Created by virjar on 17/7/8.
 */
public abstract class StartEndFunction implements StringFunction {
    @Override
    public Boolean call(StringContext stringContext, List<SyntaxNode> params) {
        Preconditions.checkArgument(params.size() >= 2, determineFunctionName() + " must have 2 parameters at last");
        String input;
        String searchString;
        boolean ignoreCase = false;

        Object calculate = params.get(0).calculate(stringContext);
        if (calculate instanceof CharSequence) {
            input = calculate.toString();
        } else if (calculate instanceof Strings) {
            Strings strings = (Strings) calculate;
            if (strings.size() == 0) {
                return false;
            }
            input = strings.get(0);
        } else {
            throw new IllegalStateException(
                    "first parameter for " + determineFunctionName() + " must be a string ,now is" + calculate);
        }

        calculate = params.get(1).calculate(stringContext);
        if (calculate instanceof CharSequence) {
            searchString = calculate.toString();
        } else if (calculate instanceof Strings) {
            Strings strings = (Strings) calculate;
            if (strings.size() == 0) {
                return false;
            }
            searchString = strings.get(0);
        } else {
            throw new IllegalStateException(
                    "second parameter for " + determineFunctionName() + " must be a string ,now is" + calculate);
        }

        if (params.size() >= 3) {
            Object ignoreCaseObject = params.get(2).calculate(stringContext);
            if (!(ignoreCaseObject instanceof Boolean)) {
                throw new IllegalStateException("third parameter for " + determineFunctionName()
                        + " must be boolean ,now is " + ignoreCaseObject);
            }
            ignoreCase = (boolean) ignoreCaseObject;
        }
        return handle(input, searchString, ignoreCase);

    }

    protected abstract boolean handle(String input, String searchString, boolean ignoreCase);

}
