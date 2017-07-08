package com.virjar.vscrawler.core.selector.string.function;

import java.util.List;

import com.google.common.base.Preconditions;
import com.virjar.vscrawler.core.selector.string.Strings;
import com.virjar.vscrawler.core.selector.string.syntax.StringContext;
import com.virjar.vscrawler.core.selector.string.syntax.SyntaxNode;

/**
 * Created by virjar on 17/7/8.
 */
public abstract class ISFunction implements StringFunction {
    @Override
    public Object call(StringContext stringContext, List<SyntaxNode> params) {
        Preconditions.checkArgument(params.size() >= 1, determineFunctionName() + ":函数至少包含2个参数");

        Object string = params.get(0).calculate(stringContext);
        if (string == null) {
            return new Strings();
        }

        return handle(string.toString());
    }

    protected abstract Integer handle(String input);

}
