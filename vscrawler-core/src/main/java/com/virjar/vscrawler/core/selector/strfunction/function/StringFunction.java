package com.virjar.vscrawler.core.selector.strfunction.function;

import java.util.List;

import com.virjar.vscrawler.core.selector.strfunction.Strings;
import com.virjar.vscrawler.core.selector.strfunction.syntax.StringContext;
import com.virjar.vscrawler.core.selector.strfunction.syntax.SyntaxNode;

/**
 * Created by virjar on 17/7/8.
 */
public interface StringFunction {
    Strings call(StringContext stringContext, List<SyntaxNode> params);

    String determineFunctionName();
}
