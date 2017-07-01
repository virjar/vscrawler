package com.virjar.vscrawler.core.selector.string.function;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.virjar.vscrawler.core.selector.string.tree.IntegerType;
import com.virjar.vscrawler.core.selector.string.tree.ParamType;
import com.virjar.vscrawler.core.selector.string.tree.StringFunction;
import com.virjar.vscrawler.core.selector.string.tree.StringType;

/**
 * Created by virjar on 17/7/1.<br/>
 * 对字符串进行正则规则叠加
 */
public class RegexStringFunction implements StringFunction {
    private ConcurrentMap<String, Pattern> cache = Maps.newConcurrentMap();
    private static final int cacheSize = 1024;

    private Pattern compileRegex(String regex) {
        if (cache.containsKey(regex)) {
            return cache.get(regex);
        }
        Pattern ret = Pattern.compile(regex);
        if (cache.size() < cacheSize) {
            cache.putIfAbsent(regex, ret);
        }
        return ret;
    }

    @Override
    public List<String> call(String from, List<ParamType> params) {
        Preconditions.checkArgument(params.size() > 0, "regex function must has one parameter");
        ParamType paramType = params.get(0);

        int group = 0;
        if (params.size() > 1) {
            ParamType groupType = params.get(1);
            if (groupType instanceof IntegerType) {
                group = ((IntegerType) groupType).getValue();
            } else if (groupType instanceof StringType) {
                group = NumberUtils.toInt(groupType.toString());
            }
        }

        Matcher matcher = compileRegex(paramType.toString()).matcher(from);
        List<String> ret = Lists.newLinkedList();
        while (matcher.find()) {
            ret.add(matcher.group(group));
        }
        return ret;
    }

    @Override
    public String getName() {
        return "regex";
    }
}
