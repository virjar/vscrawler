package com.virjar.vscrawler.core.selector.table.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.virjar.vscrawler.core.selector.table.KeyResolver;
import com.virjar.vscrawler.core.util.ChineseUtil;

/**
 * Created by mario1oreo on 2017/6/3.
 */
public class KeyResolverDefault implements KeyResolver {

    Map<String, String> keyMapping = new HashMap<String, String>();

    public KeyResolverDefault(Map<String, String> keyMapping) {
        this.keyMapping = keyMapping;
    }

    @Override
    public String keyParser(String pageKey) {
        if (keyMapping.containsKey(pageKey)) {
            return keyMapping.get(pageKey);
        }
        String pinyinKey = StringUtils.EMPTY;
        if (!ChineseUtil.matches("[\\u4e00-\\u9fa5]+", pageKey)) {
            pinyinKey = ChineseUtil.converterToSpell(ChineseUtil.matchesChineseValue(pageKey));
        } else {
            pinyinKey = ChineseUtil.converterToSpell(pageKey);
        }
        keyMapping.put(pageKey, pinyinKey);
        return pinyinKey;
    }
}
