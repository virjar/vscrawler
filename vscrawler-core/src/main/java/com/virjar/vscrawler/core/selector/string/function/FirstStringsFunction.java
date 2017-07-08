package com.virjar.vscrawler.core.selector.string.function;

import java.util.List;

import com.virjar.vscrawler.core.selector.string.Strings;
import com.virjar.vscrawler.core.selector.string.syntax.StringContext;
import com.virjar.vscrawler.core.selector.string.syntax.SyntaxNode;

/**
 * Created by virjar on 17/7/8.
 */
public abstract class FirstStringsFunction implements StringFunction {
    @Override
    public Object call(StringContext stringContext, List<SyntaxNode> params) {
        if (params.size() == 0) {
            return new Strings();
        }
        Object o = params.get(0).calculate(stringContext);
        if (o == null) {
            return new Strings();
        }
        if (o instanceof Strings) {
            return handle((Strings) o, stringContext, params);
        } else {
            return handle(new Strings((String) o), stringContext, params);
        }
    }

    protected  abstract Strings handle(Strings input, StringContext stringContext, List<SyntaxNode> params);

}
