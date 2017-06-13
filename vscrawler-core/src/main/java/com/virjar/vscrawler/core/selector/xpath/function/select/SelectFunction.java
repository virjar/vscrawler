package com.virjar.vscrawler.core.selector.xpath.function.select;

import java.util.List;

import org.jsoup.select.Elements;

import com.virjar.vscrawler.core.selector.xpath.function.NameAware;
import com.virjar.vscrawler.core.selector.xpath.model.JXNode;
import com.virjar.vscrawler.core.selector.xpath.model.XpathNode;

/**
 * Created by virjar on 17/6/6.
 */
public interface SelectFunction extends NameAware {
    List<JXNode> call(XpathNode.ScopeEm scopeEm, Elements elements, List<String> args);

}
