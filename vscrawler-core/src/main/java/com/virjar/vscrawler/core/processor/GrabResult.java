package com.virjar.vscrawler.core.processor;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.*;

/**
 * Created by virjar on 2018/1/20.
 * <br>
 * 还是使用object存储抓取结果,避免一些不必要的转换
 *
 * @author virjar
 * @since 0.2.6
 */
public class GrabResult extends CrawlResult {
    private List<Object> entityResult = Lists.newLinkedList();
    private Map<String, Object> fieldMap = Maps.newHashMap();

    public void putField(String key, Object value) {
        fieldMap.put(key, value);
    }

    @Override
    public void addResult(String result) {
        entityResult.add(result);
    }

    public void addResult(Object entity) {
        entityResult.add(entity);
    }

    @Override
    public void addResults(Collection<?> resultsIn) {
        entityResult.addAll(resultsIn);
    }

    @Override
    public List<String> allResult() {
        List<String> entities = Lists.transform(entityResult, new Function<Object, String>() {
            @Override
            public String apply(Object input) {
                if (input instanceof CharSequence) {
                    return input.toString();
                }
                return JSONObject.toJSONString(input);
            }
        });
        if (fieldMap.size() == 0) {
            return entities;
        }
        ArrayList<String> ret = Lists.newArrayListWithExpectedSize(entities.size() + 1);
        ret.addAll(entities);
        ret.add(JSONObject.toJSONString(fieldMap));
        return ret;
    }

    public List<Object> allEntityResult() {
        if (fieldMap.size() == 0) {
            return Lists.newArrayList(entityResult);
        }
        LinkedList<Object> objects = Lists.newLinkedList(entityResult);
        objects.add(fieldMap);
        return objects;
    }

    public Object getFiled(String key) {
        return fieldMap.get(key);
    }

    public Map<String, Object> allFiled() {
        return Maps.newHashMap(fieldMap);
    }
}
