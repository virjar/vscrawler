package com.virjar.vscrawler.core.util;

import com.google.common.collect.Maps;

import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

/**
 * Created by virjar on 2018/1/7.<br/>
 * 统一缓存正则编译
 *
 * @author virjar
 * @since 0.2.2
 */
public class CatchRegexPattern {
    private static final int cacheSize = 1024;
    private static ConcurrentMap<String, Pattern> cache = Maps.newConcurrentMap();

    public static Pattern compile(String regex) {
        Pattern pattern = cache.get(regex);
        if (pattern != null) {
            return pattern;
        }
        if (cache.size() > cacheSize) {
            return Pattern.compile(regex);
        }
        synchronized (CatchRegexPattern.class) {
            if (cache.containsKey(regex)) {
                return cache.get(regex);
            }
            pattern = Pattern.compile(regex);
            cache.put(regex, pattern);
        }
        return pattern;
    }
}
