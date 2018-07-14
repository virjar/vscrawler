package com.virjar.vscrawler.core.resourcemanager.storage.ram;

import com.google.common.collect.Maps;
import com.virjar.vscrawler.core.resourcemanager.model.ResourceItem;
import com.virjar.vscrawler.core.resourcemanager.storage.ForbiddenQueueStore;

import java.util.List;
import java.util.Map;

/**
 * Created by virjar on 2018/7/14.
 */
public class RamForbiddenQueueStore implements ForbiddenQueueStore {

    private Map<String, Map<String, ResourceItem>> queueMaps = Maps.newConcurrentMap();

    private Map<String, ResourceItem> createOrGet(String queueID) {
        Map<String, ResourceItem> holderSortedSet = queueMaps.get(queueID);
        if (holderSortedSet != null) {
            return holderSortedSet;
        }
        synchronized (this) {
            if (queueMaps.containsKey(queueID)) {
                return queueMaps.get(queueID);
            }
            holderSortedSet = Maps.newConcurrentMap();
            queueMaps.put(queueID, holderSortedSet);
            return holderSortedSet;
        }
    }

    @Override
    public void add(String queueID, ResourceItem resourceItem) {
        createOrGet(queueID).put(resourceItem.getKey(), resourceItem);
    }

    @Override
    public long size(String queueID) {
        return createOrGet(queueID).size();
    }

    @Override
    public ResourceItem remove(String queueID, String key) {
        return createOrGet(queueID).remove(key);
    }


    @Override
    public void clear(String queueID) {
        createOrGet(queueID).clear();
    }

    @Override
    public List<ResourceItem> queryAll(String queueID) {
        return null;
    }
}
