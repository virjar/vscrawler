package com.virjar.vscrawler.core.selector.string.function;

import java.util.List;

import com.virjar.vscrawler.core.selector.string.Strings;
import com.virjar.vscrawler.core.selector.string.syntax.StringContext;
import com.virjar.vscrawler.core.selector.string.syntax.SyntaxNode;

/**
 * Created by virjar on 17/7/8.
 */
public abstract class SSFunction extends FirstStringsFunction {

    protected Strings handle(Strings input, StringContext stringContext, List<SyntaxNode> params) {
        Strings ret = new Strings();
        for (String str : input) {
            ret.add(handleSingleStr(str));
        }
        return ret;
    }

    protected abstract String handleSingleStr(String input);
}
