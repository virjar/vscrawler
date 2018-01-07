package com.virjar.vscrawler.resourcemanager.service;

import com.virjar.vscrawler.resourcemanager.model.ResourceItem;

/**
 * Created by virjar on 2018/1/4.<br/>
 *
 * @author virjar
 * @since 0.2.2
 * 支持评分的队列,负责存储层
 */
public interface StoreQueue {
    long size(String queueID);

    boolean addFirst(String queueID, ResourceItem e);

    boolean addLast(String queueID, ResourceItem e);

    boolean addIndex(String queueID, long index, ResourceItem e);

    ResourceItem poll(String queueID);

    ResourceItem take(String queueID);

    ResourceItem take(String queueID, long timeOut);

    ResourceItem get(String queueID, String key);

    long index(String queueID, String key);

    boolean update(String queueID, String key, ResourceItem e);

    ResourceItem remove(String queueID, String key);
}
