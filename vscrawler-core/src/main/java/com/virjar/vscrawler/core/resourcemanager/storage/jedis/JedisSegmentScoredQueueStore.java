package com.virjar.vscrawler.core.resourcemanager.storage.jedis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.virjar.dungproxy.client.util.CommonUtil;
import com.virjar.vscrawler.core.resourcemanager.model.ResourceItem;
import com.virjar.vscrawler.core.resourcemanager.storage.ScoredQueueStore;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by virjar on 2018/1/14.<br>
 * 可以支持分段的redis存储队列,避免redis性能问题
 * 不建议使用
 */
@Slf4j
@Deprecated
public class JedisSegmentScoredQueueStore implements ScoredQueueStore {
    // 存放key的轮询数据
    private static final String jedisPoolSuffix = "_jedis_polling";
    // 存放数据
    private static final String jedisDataSuffix = "_jedis_data";

    private static final String jedisLockKeySuffix = "_vscrawler_resourceManager_queue_lock";

    // 数据分片,每个片的大小
    private static final int blockSize = 2048;
    private static final String jedisSliceQueue = "_jedis_slice_queue";

    private static final long lockWaitTimeStamp = 1000 * 60 * 2;

    private JedisPool jedisPool;

    public JedisSegmentScoredQueueStore(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    private String makeSliceQueueKey(String queueID) {
        return queueID + jedisSliceQueue;
    }

    private String makePoolQueueKey(String queueID, String sliceID) {
        return queueID + sliceID + jedisPoolSuffix;
    }

    private String makeDataKey(String queueID) {
        return queueID + jedisDataSuffix;
    }

    private InheritableThreadLocal<AtomicInteger> locked = new InheritableThreadLocal<>();

    private String makeRedisLockKey(String queueID) {
        return queueID + jedisLockKeySuffix;
    }

    private boolean lockQueue(String queueID) {
        if (locked.get() == null) {
            synchronized (this) {
                if (locked.get() == null) {
                    locked.set(new AtomicInteger(0));
                }
            }
        }
        boolean ret = false;
        @Cleanup Jedis jedis = jedisPool.getResource();
        if (locked.get().incrementAndGet() > 1) {
            return true;
        }
        String redisLockKey = makeRedisLockKey(queueID);
        long lockRequestTime = System.currentTimeMillis();
        while (true) {
            String result = jedis.set(redisLockKey, "lockTheQueue", "NX", "EX", 120);
            if (StringUtils.isNotEmpty(result) && result.equalsIgnoreCase("OK")) {
                ret = true;
                break;
            }
            if (lockRequestTime + lockWaitTimeStamp < System.currentTimeMillis()) {
                locked.get().decrementAndGet();
                break;
            }
            long sleepTime = jedis.ttl(redisLockKey) * 1000 - 10;
            if (sleepTime > lockRequestTime) {
                return false;
            }
            if (sleepTime > 0) {
                CommonUtil.sleep(sleepTime);
            }
        }
        return ret;
    }

    private void unLockQueue(String queueID) {
        if (locked.get() == null) {
            return;
        }
        if (locked.get().decrementAndGet() > 0) {
            return;
        }
        @Cleanup Jedis jedis = jedisPool.getResource();
        jedis.del(makeRedisLockKey(queueID));
        locked.remove();
    }

    @Override
    public long size(String queueID) {
        @Cleanup Jedis jedis = jedisPool.getResource();
        return jedis.hlen(makeDataKey(queueID));
    }

    @Override
    public boolean addFirst(String queueID, ResourceItem e) {
        if (!lockQueue(queueID)) {
            return false;
        }
        @Cleanup Jedis jedis = jedisPool.getResource();
        try {
            remove(queueID, e.getKey());
            jedis.hset(makeDataKey(queueID), e.getKey(), JSONObject.toJSONString(e));
            String sliceID = jedis.lpop(makeSliceQueueKey(queueID));
            if (isNil(sliceID)) {
                sliceID = "1";
            }
            jedis.lpush(makeSliceQueueKey(queueID), sliceID);
            jedis.lpush(makePoolQueueKey(queueID, sliceID), e.getKey());
        } finally {
            unLockQueue(queueID);
        }
        return true;
    }

    @Override
    public boolean addLast(String queueID, ResourceItem e) {
        if (!lockQueue(queueID)) {
            return false;
        }
        @Cleanup Jedis jedis = jedisPool.getResource();
        try {
            remove(queueID, e.getKey());
            jedis.hset(makeDataKey(queueID), e.getKey(), JSONObject.toJSONString(e));
            String sliceID = jedis.rpop(makeSliceQueueKey(queueID));
            if (isNil(sliceID)) {
                sliceID = "1";
            }
            jedis.rpush(makeSliceQueueKey(queueID), sliceID);
            jedis.rpush(makePoolQueueKey(queueID, sliceID), e.getKey());
        } finally {
            unLockQueue(queueID);
        }
        return true;
    }

    @Override
    public boolean addIndex(String queueID, long index, ResourceItem e) {
        if (!lockQueue(queueID)) {
            return false;
        }
        @Cleanup Jedis jedis = jedisPool.getResource();
        try {
            remove(queueID, e.getKey());
            // block 从1开始计数
            int block = blockID(index + 1);
            List<String> sliceQueue = sliceQueue(queueID);
            String sliceID;
            if (block - 1 < sliceQueue.size()) {
                sliceID = sliceQueue.get(block - 1);
            } else {
                // create a new slice
                sliceID = String.valueOf(block);
                if (!sliceQueue.contains(sliceID)) {
                    Preconditions.checkArgument(index <= size(queueID));
                    jedis.rpush(makeSliceQueueKey(queueID), sliceID);
                } else {
                    sliceID = sliceQueue.get(sliceQueue.size() - 1);
                }
            }
            String poolQueueKey = makePoolQueueKey(queueID, sliceID);
            Long length = jedis.llen(poolQueueKey);
            long offset = blockOffset(index);
            if (offset <= length) {
                offset = length - 1;
            }
            String position = jedis.lindex(makePoolQueueKey(queueID, sliceID), offset);
            if (isNil(position)) {
                jedis.rpush(poolQueueKey, e.getKey());
            } else {
                jedis.linsert(poolQueueKey, BinaryClient.LIST_POSITION.AFTER, position, e.getKey());
            }
            jedis.hset(makeDataKey(queueID), e.getKey(), JSONObject.toJSONString(e));
        } finally {
            unLockQueue(queueID);
        }
        return true;
    }

    private boolean isNil(String input) {
        return StringUtils.equalsIgnoreCase(input, "nil") || StringUtils.isBlank(input);
    }

    @Override
    public ResourceItem poll(String queueID) {
        if (!lockQueue(queueID)) {
            return null;
        }
        @Cleanup Jedis jedis = jedisPool.getResource();
        try {
            String sliceQueueKey = makeSliceQueueKey(queueID);
            String firstResourceKey;
            String firstSliceID = null;
            while (true) {
                String sliceID = jedis.lpop(sliceQueueKey);
                if (isNil(sliceID)) {
                    // empty queue
                    return null;
                }
                if (firstSliceID == null) {
                    firstSliceID = sliceID;
                } else if (firstSliceID.equals(sliceID)) {
                    // 这个条件下,数据紊乱了?
                    jedis.lpush(sliceQueueKey, firstSliceID);
                    return null;
                }
                firstResourceKey = jedis.lpop(makePoolQueueKey(queueID, sliceID));
                if (!isNil(firstResourceKey)) {
                    jedis.lpush(sliceQueueKey, sliceID);
                    break;
                }
                jedis.rpush(sliceQueueKey, sliceID);
            }

            String dataJson = jedis.hget(makeDataKey(queueID), firstResourceKey);
            if (isNil(dataJson)) {
                throw new IllegalStateException(
                        "this is no meta data for key queue :" + queueID + " ,for resourceKey :" + firstResourceKey);
            }
            jedis.hdel(makeDataKey(queueID), firstResourceKey);
            return JSONObject.toJavaObject(JSON.parseObject(dataJson), ResourceItem.class);
        } finally {
            unLockQueue(queueID);
        }

    }

    @Override
    public ResourceItem get(String queueID, String key) {
        if (!lockQueue(queueID)) {
            return null;
        }
        @Cleanup Jedis jedis = jedisPool.getResource();
        try {
            String dataJson = jedis.hget(makeDataKey(queueID), key);
            if (isNil(dataJson)) {
                return null;
            }
            return JSONObject.toJavaObject(JSON.parseObject(dataJson), ResourceItem.class);
        } finally {
            unLockQueue(queueID);
        }
    }

    /**
     * 此操作可能消耗资源较多,因为需要传递所有数据到内存,如果遇到问题在考虑优化吧
     *
     * @param queueID 队列id
     * @param key     待查找的资源
     * @return 该资源当前在队列的位置, 如果该资源在队列中不存在, 则返回-1
     */
    @Override
    public long index(String queueID, String key) {
        if (!lockQueue(queueID)) {
            return -1;
        }
        try {
            return queue(queueID).indexOf(key);
        } finally {
            unLockQueue(queueID);
        }
    }

    @Override
    public boolean update(String queueID, ResourceItem e) {
        if (!lockQueue(queueID)) {
            return false;
        }
        @Cleanup Jedis jedis = jedisPool.getResource();
        try {
            String dataJson = jedis.hget(makeDataKey(queueID), e.getKey());
            jedis.hset(makeDataKey(queueID), e.getKey(), JSONObject.toJSONString(e));
            return !isNil(dataJson);
        } finally {
            unLockQueue(queueID);
        }
    }

    @Override
    public ResourceItem remove(String queueID, String key) {
        if (!lockQueue(queueID)) {
            return null;
        }
        @Cleanup Jedis jedis = jedisPool.getResource();
        try {
            String dataJson = jedis.hget(makeDataKey(queueID), key);
            if (isNil(dataJson)) {
                return null;
            }
            jedis.hdel(makeDataKey(queueID), key);
            // lrem很消耗资源,尽量减少该命令操作
            for (String slice : sliceQueue(queueID)) {
                if (jedis.lrem(makePoolQueueKey(queueID, slice), 1, key) > 0) {
                    break;
                }
            }
            return JSONObject.toJavaObject(JSON.parseObject(dataJson), ResourceItem.class);
        } finally {
            unLockQueue(queueID);
        }
    }

    @Override
    public void addBatch(String queueID, Set<ResourceItem> resourceItems) {
        if (!lockQueue(queueID)) {
            return;
        }
        @Cleanup Jedis jedis = jedisPool.getResource();
        final String dataKey = makeDataKey(queueID);
        try {
            final Set<String> hkeys = jedis.hkeys(dataKey);
            Set<ResourceItem> filterSet = Sets.filter(resourceItems, new Predicate<ResourceItem>() {
                @Override
                public boolean apply(ResourceItem input) {
                    return !hkeys.contains(input.getKey());
                }
            });
            List<String> sliceQueue = sliceQueue(queueID);
            Set<String> newSlices = Sets.newHashSet();
            long index = size(queueID) + 1;
            String tailSlice = null;
            for (ResourceItem resourceItem : filterSet) {
                jedis.hset(dataKey, resourceItem.getKey(), JSONObject.toJSONString(resourceItem));
                String sliceID = String.valueOf(blockID(index));
                if (sliceID.equals(tailSlice) || sliceQueue.contains(sliceID)) {
                    sliceID = sliceQueue.get(sliceQueue.size() - 1);
                    tailSlice = sliceID;
                } else if (!newSlices.contains(sliceID)) {
                    jedis.rpush(makeSliceQueueKey(queueID), sliceID);
                    newSlices.add(sliceID);
                }
                jedis.rpush(makePoolQueueKey(queueID, sliceID), resourceItem.getKey());
                index++;
            }
        } finally {
            unLockQueue(queueID);
        }
    }

    private int blockID(long index) {
        return (int) ((index + blockSize - 1) / blockSize);
    }

    private long blockOffset(long index) {
        return index % blockSize;
    }

    @Override
    public Set<String> notExisted(String queueID, Set<String> resourceItemKeys) {
        if (!lockQueue(queueID)) {
            return Collections.emptySet();
        }
        @Cleanup Jedis jedis = jedisPool.getResource();
        try {
            final Set<String> existedSet = jedis.hkeys(makeDataKey(queueID));
            return Sets.filter(resourceItemKeys, new Predicate<String>() {
                @Override
                public boolean apply(String input) {
                    return !existedSet.contains(input);
                }
            });
        } finally {
            unLockQueue(queueID);
        }
    }

    @Override
    public void clear(String queueID) {
        if (!lockQueue(queueID)) {
            return;
        }
        @Cleanup Jedis jedis = jedisPool.getResource();
        try {
            for (String sliceID : sliceQueue(queueID)) {
                jedis.del(makePoolQueueKey(queueID, sliceID));
            }
            jedis.del(makeDataKey(queueID));
            jedis.del(makeSliceQueueKey(queueID));
        } finally {
            unLockQueue(queueID);
        }
    }

    private List<String> queue(String queueID) {
        @Cleanup Jedis jedis = jedisPool.getResource();
        List<String> queue = Lists.newLinkedList();
        for (String slice : sliceQueue(queueID)) {
            queue.addAll(jedis.lrange(makePoolQueueKey(queueID, slice), 0, -1));
        }
        return queue;
    }

    @Override
    public List<ResourceItem> queryAll(String queueID) {
        if (!lockQueue(queueID)) {
            throw new IllegalStateException("error for acquire queue lock for queue :" + queueID);
        }
        @Cleanup Jedis jedis = jedisPool.getResource();
        try {
            final Map<String, String> map = jedis.hgetAll(makeDataKey(queueID));

            return Lists.transform(queue(queueID), new Function<String, ResourceItem>() {
                @Override
                public ResourceItem apply(String input) {
                    return JSONObject.toJavaObject(JSONObject.parseObject(map.get(input)), ResourceItem.class);
                }
            });
        } finally {
            unLockQueue(queueID);
        }
    }

    private List<String> sliceQueue(String queueID) {
        if (!lockQueue(queueID)) {
            throw new IllegalStateException("error for acquire queue lock for queue :" + queueID);
        }
        @Cleanup Jedis jedis = jedisPool.getResource();
        try {
            return jedis.lrange(makeSliceQueueKey(queueID), 0, -1);
        } finally {
            unLockQueue(queueID);
        }
    }
}
