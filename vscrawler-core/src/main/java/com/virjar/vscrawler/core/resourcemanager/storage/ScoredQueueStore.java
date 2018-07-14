package com.virjar.vscrawler.core.resourcemanager.storage;

import com.virjar.vscrawler.core.resourcemanager.model.ResourceItem;

import java.util.Set;

/**
 * Created by virjar on 2018/1/4.<br/>
 *
 * @author virjar
 * @since 0.2.2
 * 支持评分的队列,负责存储层
 */
public interface ScoredQueueStore extends BaseStorage {

    boolean addFirst(String queueID, ResourceItem e);

    boolean addLast(String queueID, ResourceItem e);

    boolean addIndex(String queueID, long index, ResourceItem e);

    ResourceItem pop(String queueID);

    long index(String queueID, String key);

    void addBatch(String queueID, Set<ResourceItem> resourceItems);


}
