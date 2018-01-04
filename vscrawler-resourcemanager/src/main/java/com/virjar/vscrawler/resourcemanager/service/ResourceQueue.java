package com.virjar.vscrawler.resourcemanager.service;

import com.virjar.vscrawler.resourcemanager.model.ResourceItem;

/**
 * Created by virjar on 2018/1/4.
 */
public class ResourceQueue {
    private String tag;

    public ResourceQueue(String tag) {
        this.tag = tag;
    }

    /**
     * 得到一个资源
     *
     * @return 资源管理器当前分发的资源, 如果系统不能分发资源, 则该返回可能为null
     */
    public ResourceItem allocate() {
        return null;
    }

    /**
     * 反馈某个资源的使用状况
     *
     * @param key  每个资源都应该有一个key
     * @param isOK 该资源状态,可用还是不可用
     */
    public void feedBack(String key, boolean isOK) {

    }

    /**
     * 永久封禁某个资源
     *
     * @param key 每个资源都应该有一个key
     */
    public void forbidden(String key) {

    }

    /**
     * 解除永久封禁
     *
     * @param key 每个资源都应该有一个key
     */
    public void unForbidden(String key) {

    }
}
