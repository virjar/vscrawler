package com.virjar.vscrawler.core.resourcemanager.storage;

import com.virjar.vscrawler.core.resourcemanager.model.ResourceItem;

import java.util.List;
import java.util.Set;

/**
 * Created by virjar on 2018/7/14.<br>
 * 资源管理器存储模型的高层抽象
 *
 * @author virjar
 * @since 0.3.2
 */
public interface BaseStorage {
    /**
     * 返回容器资源个数
     *
     * @return 个数
     */
    long size(String queueID);

    /**
     * 清空队列数据
     *
     * @param queueID 队列id
     */
    void clear(String queueID);

    /**
     * 查询该队列所有资源,主要用于运维工作,该api留作后门,平常不建议使用
     *
     * @param queueID 队列id
     * @return 所有的资源item
     */
    List<ResourceItem> queryAll(String queueID);

    ResourceItem remove(String queueID, String key);

    ResourceItem get(String queueID, String key);

    boolean update(String queueID, ResourceItem e);

    Set<String> notExisted(String queueID, Set<String> resourceItemKeys);
}
