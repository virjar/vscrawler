package com.virjar.vscrawler.core.selector.string.function;

import java.util.List;

import com.virjar.vscrawler.core.selector.string.syntax.StringContext;
import com.virjar.vscrawler.core.selector.string.syntax.SyntaxNode;

/**
 * Created by virjar on 17/7/8.
 */
public class RuntimeString implements StringFunction {
    @Override
    public String call(StringContext stringContext, List<SyntaxNode> params) {
        return stringContext.getData();
    }

    @Override
    public String determineFunctionName() {
        return "runtimeString";
    }
}
