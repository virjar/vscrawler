package com.virjar.vscrawler.core.resourcemanager.storage;

import com.virjar.vscrawler.core.resourcemanager.model.ResourceItem;

/**
 * Created by virjar on 2018/7/13.<br>
 * 该队列,以大小排序item,防止对redis过度遍历循环
 *
 * @author virjar
 * @since 0.3.2
 */
public interface BlockingQueueStore extends BaseStorage {


    /**
     * 增加一条记录,根据分数对他进行排序
     *
     * @param resourceItem 资源body
     */
    void zadd(String queueID, ResourceItem resourceItem);


    /**
     * 返回队列首部第一个资源,如果容器为空,返回null。该操作不移出队列头部数据
     *
     * @return 队列首部第一个资源
     */
    ResourceItem poll(String queueID);


    /**
     * 返回队列首部第一个资源,如果容易为空,返回null。该操作将会移出队列头部数据
     *
     * @return 队列首部第一个资源
     */
    ResourceItem pop(String queueID);


}
