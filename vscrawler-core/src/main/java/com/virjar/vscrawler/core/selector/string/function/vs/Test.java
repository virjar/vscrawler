package com.virjar.vscrawler.core.selector.string.function.vs;

import java.util.List;

import com.google.common.base.Preconditions;
import com.virjar.vscrawler.core.selector.string.function.StringFunction;
import com.virjar.vscrawler.core.selector.string.syntax.StringContext;
import com.virjar.vscrawler.core.selector.string.syntax.SyntaxNode;

/**
 * Created by virjar on 17/7/8.替代三目表达式
 * 
 * @since 1.1.0
 * @author virjar
 */
public class Test implements StringFunction {
    @Override
    public Object call(StringContext stringContext, List<SyntaxNode> params) {
        Preconditions.checkArgument(params.size() >= 3, determineFunctionName() + " must have 3 parameters at lest");

        Object conditionObject = params.get(0).calculate(stringContext);
        if (!(conditionObject instanceof Boolean)) {
            throw new IllegalStateException(
                    "first parameter for " + determineFunctionName() + " must be boolean,now is " + conditionObject);
        }
        return (Boolean) conditionObject ? params.get(1).calculate(stringContext)
                : params.get(2).calculate(stringContext);
    }

    @Override
    public String determineFunctionName() {
        return "test";
    }
}
