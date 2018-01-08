package com.virjar.vscrawler.resourcemanager.service;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.virjar.vscrawler.resourcemanager.model.ResourceItem;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by virjar on 2018/1/7.<br/>基于内存的队列存储,满足各自队列并发加锁隔离,单个队列操作全程加锁
 */
public class RamQueueStore implements QueueStore {
    private Map<String, InnerList> queueMaps = Maps.newConcurrentMap();

    private static class InnerList extends LinkedList<ResourceItem> {
        private Map<String, ResourceItem> maps = Maps.newConcurrentMap();
        private ReentrantLock lock = new ReentrantLock();

        @Override
        public int size() {
            lock.lock();
            try {
                return super.size();
            } finally {
                lock.unlock();
            }
        }

        private void removeFromList(String key) {
            lock.lock();
            try {
                if (!maps.containsKey(key)) {
                    return;
                }
                Iterator<ResourceItem> iterator = iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().getKey().equals(key)) {
                        iterator.remove();
                    }
                }
            } finally {
                lock.unlock();
            }
        }

        private void prepareAdd(ResourceItem resourceItem) {
            removeFromList(resourceItem.getKey());
            maps.put(resourceItem.getKey(), resourceItem);
        }

        private void afterRemove(ResourceItem resourceItem) {
            maps.remove(resourceItem.getKey());
        }

        @Override
        public void addFirst(ResourceItem resourceItem) {
            lock.lock();
            try {
                prepareAdd(resourceItem);
                super.addFirst(resourceItem);
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void addLast(ResourceItem resourceItem) {
            lock.lock();
            try {
                prepareAdd(resourceItem);
                super.addLast(resourceItem);
            } finally {
                lock.unlock();
            }
        }

        void addIndex(long index, ResourceItem resourceItem) {
            lock.lock();
            try {
                prepareAdd(resourceItem);
                //对于我们来说,顺序只是确定优先级,可以容忍数据错误
                if (index > size()) {
                    index = size();
                }
                if (index < 0) {
                    index = 0;
                }
                add((int) index, resourceItem);
            } finally {
                lock.unlock();
            }
        }

        @Override
        public ResourceItem removeFirst() {
            lock.lock();
            try {
                if (size() == 0) {
                    return null;
                }
                ResourceItem resourceItem = super.removeFirst();
                afterRemove(resourceItem);
                return resourceItem;
            } finally {
                lock.unlock();
            }
        }

        ResourceItem get(String key) {
            return maps.get(key);
        }

        int indexOf(String key) {
            return indexOf(get(key));
        }

        ResourceItem removeByKey(String key) {
            lock.lock();
            try {
                ResourceItem resourceItem = get(key);
                if (remove(resourceItem)) {
                    return resourceItem;
                }
                return null;
            } finally {
                lock.unlock();
            }
        }

        @Override
        public boolean addAll(Collection<? extends ResourceItem> c) {
            lock.lock();
            try {
                return super.addAll(Collections2.filter(c, new Predicate<ResourceItem>() {
                    @Override
                    public boolean apply(ResourceItem input) {
                        return maps.put(input.getKey(), input) == null;
                    }
                }));
            } finally {
                lock.unlock();
            }
        }
    }

    private InnerList createOrGet(String queueID) {
        InnerList innerList = queueMaps.get(queueID);
        if (innerList != null) {
            return innerList;
        }
        synchronized (this) {
            if (queueMaps.containsKey(queueID)) {
                return queueMaps.get(queueID);
            }
            innerList = new InnerList();
            queueMaps.put(queueID, innerList);
            return innerList;
        }
    }

    @Override
    public long size(String queueID) {
        return createOrGet(queueID).size();
    }

    @Override
    public boolean addFirst(String queueID, ResourceItem e) {
        createOrGet(queueID).addFirst(e);
        return true;
    }

    @Override
    public boolean addLast(String queueID, ResourceItem e) {
        createOrGet(queueID).addLast(e);
        return true;
    }

    @Override
    public boolean addIndex(String queueID, long index, ResourceItem e) {
        createOrGet(queueID).addIndex(index, e);
        return true;
    }

    @Override
    public ResourceItem poll(String queueID) {
        return createOrGet(queueID).removeFirst();
    }


    @Override
    public ResourceItem get(String queueID, String key) {
        return createOrGet(queueID).get(key);
    }

    @Override
    public long index(String queueID, String key) {
        return createOrGet(queueID).indexOf(key);
    }

    @Override
    public boolean update(String queueID, ResourceItem e) {
        //对于ram来说,update 无意义
        //createOrGet(queueID).update(e);
        return true;
    }

    @Override
    public ResourceItem remove(String queueID, String key) {
        return createOrGet(queueID).removeByKey(key);
    }

    @Override
    public void addBatch(String queueID, Set<ResourceItem> resourceItems) {
        createOrGet(queueID).addAll(resourceItems);
    }

    @Override
    public Set<String> notExisted(String queueID, Set<String> resourceItemKeys) {
        final Map<String, ResourceItem> maps = createOrGet(queueID).maps;
        return Sets.filter(resourceItemKeys, new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return !maps.containsKey(input);
            }
        });
    }
}
