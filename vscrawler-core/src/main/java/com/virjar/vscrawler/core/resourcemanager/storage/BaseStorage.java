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

    /**
     * 移出某个资源
     *
     * @param queueID 队列标记
     * @param key     资源key
     * @return 被移出的资源, 如果没有命中, 则返回null
     */
    ResourceItem remove(String queueID, String key);

    /**
     * 获取某个资源
     *
     * @param queueID 队列id
     * @param key     资源key
     * @return 命中的资源内容
     */
    ResourceItem get(String queueID, String key);

    /**
     * 更新资源,将资源数据刷新到存储,如果该存储是基于ram实现的,那么该操作无意义
     *
     * @param queueID 队列id
     * @param e       资源对象
     * @return 是否成功
     */
    boolean update(String queueID, ResourceItem e);

    /**
     * 过滤不存在的资源,主要用在资源导入,导入时需要做一次消重判断,这是由于我们的模型中将资源划分为三个队列,三个队列数据结构不一样,当导入数据时,需要同时判断三个队列是否同时存在
     *
     * @param queueID          队列id
     * @param resourceItemKeys 待导入的新资源key
     * @return 不在该队列中村的资源key
     */
    Set<String> notExisted(String queueID, Set<String> resourceItemKeys);
}
