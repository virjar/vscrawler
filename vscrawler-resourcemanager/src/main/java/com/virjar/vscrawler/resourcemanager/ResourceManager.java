package com.virjar.vscrawler.resourcemanager;

import com.google.common.collect.Maps;
import com.virjar.vscrawler.resourcemanager.model.ResourceItem;
import com.virjar.vscrawler.resourcemanager.service.ResourceQueue;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentMap;

/**
 * Created by virjar on 2018/1/4.<br/>
 *
 * @author virjar
 * @since 0.2.2
 */
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ResourceManager {

    private ConcurrentMap<String, ResourceQueue> resourceQueueConcurrentMap = Maps.newConcurrentMap();


    public void registryTag(String tag, ResourceQueue resourceQueue) {
        resourceQueueConcurrentMap.put(tag, resourceQueue);
    }

    /**
     * 得到一个资源
     *
     * @param tag 资源tag,所有资源根据tag区分资源类型
     * @return 资源管理器当前分发的资源, 如果系统不能分发资源, 则该返回可能为null
     */
    public ResourceItem allocate(String tag) {
        ResourceQueue queue = resourceQueueConcurrentMap.get(tag);
        if (queue == null) {
            log.error("no resource for tag:{}", tag);
            return null;
        }
        return queue.allocate();
    }

    /**
     * 反馈某个资源的使用状况
     *
     * @param tag  资源tag,所有资源根据tag区分资源类型
     * @param key  每个资源都应该有一个key
     * @param isOK 该资源状态,可用还是不可用
     */
    public void feedBack(String tag, String key, boolean isOK) {
        ResourceQueue queue = resourceQueueConcurrentMap.get(tag);
        if (queue == null) {
            log.error("no resource for tag:{}", tag);
            return;
        }
        queue.feedBack(key, isOK);
    }

    /**
     * 永久封禁某个资源
     *
     * @param tag 资源tag,所有资源根据tag区分资源类型
     * @param key 每个资源都应该有一个key
     */
    public void forbidden(String tag, String key) {
        ResourceQueue queue = resourceQueueConcurrentMap.get(tag);
        if (queue == null) {
            log.error("no resource for tag:{}", tag);
            return;
        }
        queue.forbidden(key);
    }

    /**
     * 解除永久封禁
     *
     * @param tag 资源tag,所有资源根据tag区分资源类型
     * @param key 每个资源都应该有一个key
     */
    public void unForbidden(String tag, String key) {
        ResourceQueue queue = resourceQueueConcurrentMap.get(tag);
        if (queue == null) {
            log.error("no resource for tag:{}", tag);
            return;
        }
        queue.unForbidden(key);
    }

    public void loadResource(String tag) {
        ResourceQueue queue = resourceQueueConcurrentMap.get(tag);
        if (queue == null) {
            log.error("no resource for tag:{}", tag);
            return;
        }
        queue.loadResource();
    }
}
