package com.virjar.vscrawler.core.resourcemanager;

import com.google.common.collect.Maps;
import com.virjar.vscrawler.core.resourcemanager.storage.ScoredQueueStore;
import com.virjar.vscrawler.core.resourcemanager.service.ResourceLoader;
import com.virjar.vscrawler.core.resourcemanager.model.ResourceSetting;

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

    public ResourceManagerFactory registryResourceQueue(String tag, ResourceSetting resourceSetting, ScoredQueueStore scoredQueueStore, ResourceLoader resourceLoader) {
        resourceQueueMap.put(tag, new ResourceQueue(tag, scoredQueueStore, resourceSetting, resourceLoader));
        return this;
    }

    public ResourceManagerFactory registryResourceQueue(ResourceQueue resourceQueue) {
        resourceQueueMap.put(resourceQueue.getTag(), resourceQueue);
        return this;
    }
}
