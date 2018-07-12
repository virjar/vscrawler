package com.virjar.vscrawler.core.util;

import com.alibaba.fastjson.JSON;

/**
 * Created by virjar on 2018/7/12.<br>
 *
 * @author virjar
 * @since 0.3.2
 * for logback framework ,i want to lazy serialize a object,because of the serialize operation my be not happen
 */
public class JSONViewWrapper {
    private Object model;
    private String theStringView = null;

    private JSONViewWrapper(Object model) {
        this.model = model;
    }

    @Override
    public String toString() {
        if (theStringView != null) {
            return theStringView;
        }
        synchronized (this) {
            theStringView = JSON.toJSONString(model);
        }
        return theStringView;
    }

    public static JSONViewWrapper wrap(Object obj) {
        if (obj == null) {
            return new JSONViewWrapper("{}");
        }
        return new JSONViewWrapper(obj);
    }
}
