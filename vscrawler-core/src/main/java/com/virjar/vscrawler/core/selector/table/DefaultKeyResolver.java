package com.virjar.vscrawler.core.selector.table;

import org.jsoup.nodes.Element;

import com.virjar.vscrawler.core.util.ChineseUtil;

/**
 * Created by virjar on 17/6/21.
 */
public class DefaultKeyResolver implements KeyResolver {
    @Override
    public String resolveKey(Element element) {
        String text = element.ownText();
        if (!ChineseUtil.matches("[\\u4e00-\\u9fa5]+", text)) {
            return ChineseUtil.converterToSpell(ChineseUtil.matchesChineseValue(text));
        } else {
            return ChineseUtil.converterToSpell(text);
        }
    }
}
