package com.virjar.vscrawler.core.selector.table.impl;

import com.virjar.vscrawler.core.selector.table.ValueResolver;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 当返回结果为uuid时，则说明映射关系没有建立。
 * 此时数据不丢失，但是也无法直接入库，可在日志中观察到。
 * Created by mario1oreo on 2017/6/3.
 */
public class ValueResolverDefault implements ValueResolver {

    Map<String, String> valueMapping = new HashMap<String, String>();

    public ValueResolverDefault(Map<String, String> valueMapping) {
        this.valueMapping = valueMapping;
    }

    @Override
    public String valueParser(String key) {
        if (valueMapping.containsKey(key)){
            return valueMapping.get(key);
        }else{
            return UUID.randomUUID().toString();
        }
    }
}
