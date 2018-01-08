package com.virjar.vscrawler.core.resourcemanager;

import com.google.common.collect.Maps;
import com.virjar.vscrawler.core.resourcemanager.service.QueueStore;
import com.virjar.vscrawler.core.resourcemanager.service.ResourceLoader;
import com.virjar.vscrawler.core.resourcemanager.model.ResourceSetting;
import com.virjar.vscrawler.core.resourcemanager.service.ResourceQueue;

import java.util.concurrent.ConcurrentMap;

/**
 * Created by virjar on 2018/1/8.<br/>
 */
public class ResourceManagerFactory {
    private ConcurrentMap<String, ResourceQueue> resourceQueueMap = Maps.newConcurrentMap();

    public static ResourceManagerFactory create() {
        return new ResourceManagerFactory();
    }

    public ResourceManager build() {
        return new ResourceManager(resourceQueueMap);
    }

    public ResourceManagerFactory registryResourceQueue(String tag, ResourceSetting resourceSetting, QueueStore queueStore, ResourceLoader resourceLoader) {
        resourceQueueMap.put(tag, new ResourceQueue(tag, queueStore, resourceSetting, resourceLoader));
        return this;
    }

    public ResourceManagerFactory registryResourceQueue(ResourceQueue resourceQueue) {
        resourceQueueMap.put(resourceQueue.getTag(), resourceQueue);
        return this;
    }
}
