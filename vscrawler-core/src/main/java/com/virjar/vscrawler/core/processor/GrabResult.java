package com.virjar.vscrawler.core.processor;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

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

    @Override
    public void addResult(String result) {
        entityResult.add(result);
    }

    public void addResult(Object entity) {
        entityResult.add(entity);
    }

    @Override
    public void addResults(Collection<Object> resultsIn) {
        entityResult.addAll(resultsIn);
    }

    @Override
    public List<String> allResult() {
        return Lists.transform(entityResult, new Function<Object, String>() {
            @Override
            public String apply(Object input) {
                if (input instanceof CharSequence) {
                    return input.toString();
                }
                return JSONObject.toJSONString(input);
            }
        });
    }

    public List<Object> allEntityResult() {
        return Lists.newArrayList(entityResult);
    }
}
