package com.virjar.vscrawler.core.resourcemanager.storage.ram;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.virjar.vscrawler.core.resourcemanager.model.ResourceItem;
import com.virjar.vscrawler.core.resourcemanager.storage.BlockingQueueStore;
import com.virjar.vscrawler.core.util.SortedList;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
        SortedList<ResourceItemHolder> sortedList = queueMaps.get(queueID);
        if (sortedList != null) {
            return sortedList;
        }
        synchronized (this) {
            if (queueMaps.containsKey(queueID)) {
                return queueMaps.get(queueID);
            }
            sortedList = new SortedList<>(ResourceItemHolder.class, callback);
            queueMaps.put(queueID, sortedList);
            return sortedList;
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
        return Lists.transform(createOrGet(queueID).toList(), new Function<ResourceItemHolder, ResourceItem>() {
            @Override
            public ResourceItem apply(ResourceItemHolder input) {
                return input.resourceItem;
            }
        });

    }

    @Override
    public ResourceItem remove(String queueID, String key) {
        return null;
    }

    @Override
    public ResourceItem get(String queueID, String key) {
        return null;
    }

    @Override
    public boolean update(String queueID, ResourceItem e) {
        return false;
    }

    @Override
    public Set<String> notExisted(String queueID, Set<String> resourceItemKeys) {
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
