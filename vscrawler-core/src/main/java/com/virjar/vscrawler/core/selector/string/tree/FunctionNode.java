package com.virjar.vscrawler.core.selector.string.tree;

import java.util.List;

/**
 * Created by virjar on 17/7/1.
 */
public interface FunctionNode {
    StringFunctionResult call(String from, List<FunctionNode> params);
}
