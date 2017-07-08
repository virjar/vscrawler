package com.virjar.vscrawler.core.selector.string.function;

import java.util.List;

import com.virjar.vscrawler.core.selector.string.Strings;
import com.virjar.vscrawler.core.selector.string.syntax.StringContext;
import com.virjar.vscrawler.core.selector.string.syntax.SyntaxNode;

/**
 * Created by virjar on 17/7/8.
 */
public abstract class SSZeroLengthFunction implements StringFunction {
    @Override
    public Object call(StringContext stringContext, List<SyntaxNode> params) {
        if (params.size() == 0) {
            return new Strings();
        }

        Object o = params.get(0).calculate(stringContext);
        if (o == null) {
            return new Strings();
        }

        Strings ret = new Strings();

        int start = 0;
        if (params.size() >= 2) {
            Object startObject = params.get(1).calculate(stringContext);
            if (!(startObject instanceof Number)) {
                throw new IllegalStateException(determineFunctionName() + " second parameter must be number");
            }
            start = ((Number) startObject).intValue();
        }

        Integer end = null;
        if (params.size() >= 3) {
            Object endObject = params.get(1).calculate(stringContext);
            if (!(endObject instanceof Number)) {
                throw new IllegalStateException(determineFunctionName() + " third parameter must be number");
            }
            end = ((Number) endObject).intValue();
        }

        if (o instanceof Strings) {
            for (String str : (Strings) o) {
                int tempEnd = end == null ? str.length() : end;
                ret.add(handle(str, start, tempEnd));
            }
        } else {
            String str = o.toString();
            int tempEnd = end == null ? str.length() : end;
            ret.add(handle(str, start, tempEnd));
        }
        return ret;

    }

    abstract String handle(String str, int start, int end);
}
