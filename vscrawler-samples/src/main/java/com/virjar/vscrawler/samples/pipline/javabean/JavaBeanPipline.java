package com.virjar.vscrawler.samples.pipline.javabean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.virjar.vscrawler.core.seed.Seed;
import com.virjar.vscrawler.core.serialize.Pipeline;

import java.util.Collection;

/**
 * Created by virjar on 2018/1/20.
 */
public abstract class JavaBeanPipline<T> implements Pipeline {
    private Class<T> clazz;

    public JavaBeanPipline(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void saveItem(Collection<String> itemJson, Seed seed) {
        saveBean(Collections2.transform(itemJson, new Function<String, T>() {
            @Override
            public T apply(String input) {
                return JSONObject.toJavaObject(JSON.parseObject(input), clazz);
            }
        }), seed);
    }

    abstract void saveBean(Collection<T> beans, Seed seed);
}
