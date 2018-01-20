package com.virjar.vscrawler.core.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.seed.Seed;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by virjar on 17/4/16.
 *
 * @author virjar
 * @since 0.0.1
 */
public class CrawlResult {
    /**
     * 一个种子可能产生多个结果
     */
    private List<String> results = Lists.newLinkedList();
    private List<Seed> newSeeds = Lists.newLinkedList();
    private List<Object> allJavaBeanResults = Lists.newLinkedList();

    public void addResult(Object entity) {
        if (entity instanceof CharSequence) {
            addResult(entity.toString());
            return;
        }
        allJavaBeanResults.add(entity);
    }

    public void addResult(String result) {
        results.add(result);
    }

    public void addResults(Collection<Object> resultsIn) {
        for (Object obj : resultsIn) {
            addResult(obj);
        }
        //results.addAll(resultsIn);
    }

    public List<String> allResult() {
        ArrayList<String> ret = Lists.newArrayList(results);
        if (allJavaBeanResults.isEmpty()) {
            return ret;
        }
        ret.addAll(Collections2.transform(allJavaBeanResults, new Function<Object, String>() {
            @Override
            public String apply(Object input) {
                return JSONObject.toJSONString(input);
            }
        }));
        return ret;
    }

    public List<Object> allJavaBeanResult() {
        List<Object> ret = Lists.newArrayList(allJavaBeanResults);
        if (results.isEmpty()) {
            return ret;
        }
        ret.addAll(results);
        return ret;
    }

    public List<String> allStringResult() {
        return Lists.newArrayList(results);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> allJavaBeanResult(final Class<T> clazz) {
        Iterable<Object> objects = Iterables.filter(allJavaBeanResults, new Predicate<Object>() {
            @Override
            public boolean apply(Object input) {
                return clazz.isAssignableFrom(input.getClass());
            }
        });

        List<T> transformedResult = Lists.newLinkedList();
        for (String str : results) {
            if (StringUtils.isBlank(str)) {
                continue;
            }
            String trim = str.trim();
            if (!StringUtils.startsWith(trim, "{") || !StringUtils.startsWith(trim, "[")) {
                continue;
            }
            try {
                Object parse = JSON.parse(trim);
                if (clazz.isAssignableFrom(parse.getClass())) {
                    transformedResult.add((T) parse);
                }
                if (parse instanceof JSONObject) {
                    JSONObject jsonObject = (JSONObject) parse;
                    T t = jsonObject.toJavaObject(clazz);
                    transformedResult.add(t);
                }
            } catch (JSONException e) {
                //ignore
            }
        }
        transformedResult.addAll(Lists.newArrayList((Iterator<? extends T>) objects));
        return transformedResult;
    }

    public void addSeed(Seed seed) {
        newSeeds.add(seed);
    }

    public void addStrSeeds(Collection<String> seeds) {
        for (String str : seeds) {
            addSeed(str);
        }
    }

    public void addSeeds(Collection<Seed> seeds) {
        for (Seed seed : seeds) {
            addSeed(seed);
        }
    }

    public void addSeed(String seed) {
        newSeeds.add(new Seed(seed));
    }

    public List<Seed> allSeed() {
        return Lists.newArrayList(newSeeds);
    }
}
