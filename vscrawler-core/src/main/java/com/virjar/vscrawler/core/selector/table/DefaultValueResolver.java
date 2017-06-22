package com.virjar.vscrawler.core.selector.table;

import org.jsoup.nodes.Element;

/**
 * Created by virjar on 17/6/21.
 */
public class DefaultValueResolver implements ValueResolver {
    @Override
    public String resolveValue(Element element) {
        return element.text().trim();
    }
}
