package com.virjar.vscrawler.core.selector.strfunction.operator;

import com.virjar.vscrawler.core.selector.strfunction.syntax.StringContext;
import com.virjar.vscrawler.core.selector.strfunction.syntax.SyntaxNode;

/**
 * Created by virjar on 17/7/8.
 */
public interface Operation {
    Object operate(SyntaxNode left, SyntaxNode right, StringContext stringContext);
}
