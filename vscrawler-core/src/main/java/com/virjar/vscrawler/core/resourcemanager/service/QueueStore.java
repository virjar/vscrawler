package com.virjar.vscrawler.core.resourcemanager.service;

import com.virjar.vscrawler.core.resourcemanager.model.ResourceItem;

import java.util.Set;

/**
 * Created by virjar on 2018/1/4.<br/>
 *
 * @author virjar
 * @since 0.2.2
 * 支持评分的队列,负责存储层
 */
public interface QueueStore {
    long size(String queueID);

    boolean addFirst(String queueID, ResourceItem e);

    boolean addLast(String queueID, ResourceItem e);

    boolean addIndex(String queueID, long index, ResourceItem e);

    ResourceItem poll(String queueID);

    ResourceItem get(String queueID, String key);

    long index(String queueID, String key);

    boolean update(String queueID, ResourceItem e);

    ResourceItem remove(String queueID, String key);

    void addBatch(String queueID, Set<ResourceItem> resourceItems);

    Set<String> notExisted(String queueID, Set<String> resourceItemKeys);

    void clear(String queueID);
}
