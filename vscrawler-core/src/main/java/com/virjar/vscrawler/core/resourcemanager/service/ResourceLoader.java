package com.virjar.vscrawler.core.resourcemanager.service;

import com.virjar.vscrawler.core.resourcemanager.model.ResourceItem;

import java.util.Collection;

/**
 * Created by virjar on 2018/1/8.<br/>
 * 数据加载器,用来导入资源数据到系统
 */
public interface ResourceLoader {
    /**
     * 数据存放在给定容器中,如果还有新数据,则应该标记返回值为true
     *
     * @param resourceItems 存放导入数据的容器
     * @return 是否还有下一批数据, 用来实现分批导入
     */
    boolean loadResource(Collection<ResourceItem> resourceItems);
}
