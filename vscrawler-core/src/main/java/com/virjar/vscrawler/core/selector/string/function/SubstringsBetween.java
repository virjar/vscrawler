package com.virjar.vscrawler.core.selector.string.function;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.virjar.vscrawler.core.selector.string.Strings;
import com.virjar.vscrawler.core.selector.string.syntax.StringContext;
import com.virjar.vscrawler.core.selector.string.syntax.SyntaxNode;

/**
 * Created by virjar on 17/7/8.
 */
public class SubstringsBetween extends FirstStringsFunction {

    @Override
    Strings handle(Strings input, StringContext stringContext, List<SyntaxNode> params) {
        Preconditions.checkArgument(params.size() >= 2, "substringsBetween must has 2 parameters at lest");

        Object openObject = params.get(1).calculate(stringContext);
        if (!(openObject instanceof CharSequence)) {
            throw new IllegalStateException(determineFunctionName() + " second parameter must bu string");
        }

        String closeString = openObject.toString();
        if (params.size() >= 3) {
            Object closeObject = params.get(2).calculate(stringContext);
            if (!(closeObject instanceof CharSequence)) {
                throw new IllegalStateException(determineFunctionName() + " second parameter must bu string");
            }
            closeString = closeObject.toString();
        }

        Strings ret = new Strings();
        for (String str : input) {
            Collections.addAll(ret, StringUtils.substringsBetween(str, openObject.toString(), closeString));
        }
        return ret;
    }

    @Override
    public String determineFunctionName() {
        return "substringsBetween";
    }
}
