package com.virjar.vscrawler.core.resourcemanager.storage;

import com.virjar.vscrawler.core.resourcemanager.model.ResourceItem;

/**
 * Created by virjar on 2018/7/13.<br>
 * 该队列,数据资源无序,不需要遍历,仅仅做资源存储,主要用来实现forbidden queue
 *
 * @author virjar
 * @since 0.3.2
 */
public interface ForbiddenQueueStore extends BaseStorage {
    void add(String queueID, ResourceItem resourceItem);
}
