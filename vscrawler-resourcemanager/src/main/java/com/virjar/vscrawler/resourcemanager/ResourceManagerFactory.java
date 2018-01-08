package com.virjar.vscrawler.resourcemanager;

import com.google.common.collect.Maps;
import com.virjar.vscrawler.resourcemanager.model.ResourceSetting;
import com.virjar.vscrawler.resourcemanager.service.ResourceLoader;
import com.virjar.vscrawler.resourcemanager.service.ResourceQueue;
import com.virjar.vscrawler.resourcemanager.service.QueueStore;

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
