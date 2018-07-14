package com.virjar.vscrawler.core.resourcemanager.storage.ram;

import com.google.common.collect.Maps;
import com.virjar.vscrawler.core.resourcemanager.model.ResourceItem;
import com.virjar.vscrawler.core.resourcemanager.storage.BlockingQueueStore;
import com.virjar.vscrawler.core.util.SortedList;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by virjar on 2018/7/14.<br>
 * element sorted with resource reuse timestamp,implement for ram
 *
 * @author virjar
 * @since 0.3.2
 */
public class RamBlockingQueueStore implements BlockingQueueStore {
    private Map<String, SortedList<ResourceItemHolder>> queueMaps = Maps.newConcurrentMap();

    private SortedList<ResourceItemHolder> createOrGet(String queueID) {
        SortedList<ResourceItemHolder> holderSortedSet = queueMaps.get(queueID);
        if (holderSortedSet != null) {
            return holderSortedSet;
        }
        synchronized (this) {
            if (queueMaps.containsKey(queueID)) {
                return queueMaps.get(queueID);
            }
            holderSortedSet = new SortedList<>(ResourceItemHolder.class, callback);
            queueMaps.put(queueID, holderSortedSet);
            return holderSortedSet;
        }
    }

    @Override
    public void zadd(String queueID, ResourceItem resourceItem) {
        createOrGet(queueID).add(new ResourceItemHolder(resourceItem));
    }

    @Override
    public long size(String queueID) {
        return createOrGet(queueID).size();
    }

    @Override
    public ResourceItem poll(String queueID) {
        try {
            return createOrGet(queueID).get(0).resourceItem;
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public ResourceItem pop(String queueID) {
        try {
            return createOrGet(queueID).removeItemAt(0).resourceItem;
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public void clear(String queueID) {
        createOrGet(queueID).clear();
    }

    @Override
    public List<ResourceItem> queryAll(String queueID) {
        return null;
    }

    private SortedList.Callback<ResourceItemHolder> callback = new SortedList.Callback<ResourceItemHolder>() {
        @Override
        public int compare(ResourceItemHolder o1, ResourceItemHolder o2) {
            return Long.valueOf(o1.resourceItem.getValidTimeStamp()).compareTo(o2.resourceItem.getValidTimeStamp());
        }

        @Override
        public void onChanged(int position, int count) {
            //do nothing
        }

        @Override
        public boolean areContentsTheSame(ResourceItemHolder oldItem, ResourceItemHolder newItem) {
            return oldItem.resourceItem.getKey().equals(newItem.resourceItem.getKey());
        }

        @Override
        public boolean areItemsTheSame(ResourceItemHolder item1, ResourceItemHolder item2) {
            return StringUtils.equalsIgnoreCase(item1.resourceItem.getData(), item2.resourceItem.getData());
        }

        @Override
        public void onInserted(int position, int count) {
            //do nothing
        }

        @Override
        public void onRemoved(int position, int count) {
            //do nothing
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            //do nothing
        }
    };

    private class ResourceItemHolder implements Comparable<ResourceItemHolder> {
        private ResourceItem resourceItem;

        ResourceItemHolder(ResourceItem resourceItem) {
            this.resourceItem = resourceItem;
        }

        @Override
        public int compareTo(ResourceItemHolder o) {
            return Long.valueOf(resourceItem.getValidTimeStamp()).compareTo(o.resourceItem.getValidTimeStamp());
        }
    }
}
