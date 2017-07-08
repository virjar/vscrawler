package com.virjar.vscrawler.core.selector.string.operator;

import com.virjar.vscrawler.core.selector.string.syntax.StringContext;
import com.virjar.vscrawler.core.selector.string.syntax.SyntaxNode;

/**
 * Created by virjar on 17/7/8.
 */
public interface Operation {
    Object operate(SyntaxNode left, SyntaxNode right, StringContext stringContext);
}
