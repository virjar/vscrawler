package com.virjar.vscrawler.util;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by virjar on 17/4/16. <br/>
 * 这个jdk1.8才支持,vscrawler支持到jdk1.7,所以单独把这个抽取出来
 * 
 * @author virjar
 * @since 0.0.1
 */
public class MapUtil {
    public static <K, V> void putIfAbsent(Map<K, V> map, K key, V value) {
        V v = map.get(key);
        // 空字符串强制认为是空数据
        if (v instanceof String && StringUtils.isBlank((String) v)) {
            v = null;
        }
        if (v == null) {
            map.put(key, value);
        }
    }
}
