package com.virjar.vscrawler.core.resourcemanager.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.virjar.dungproxy.client.util.CommonUtil;
import com.virjar.vscrawler.core.resourcemanager.model.ResourceItem;
import org.apache.commons.io.IOUtils;
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
 * Created by virjar on 2018/1/7.<br/>
 *
 * @author virjar
 * @since 0.2.2
 */
public class JedisQueueStore implements QueueStore {
    //存放key的轮询数据
    private static final String jedisPoolSuffix = "_jedis_polling";
    //存放数据
    private static final String jedisDataSuffix = "_jedis_data";

    private static final String jedisLockKeySuffix = "_vscrawler_resourceManager_queue_lock";
    private JedisPool jedisPool;

    private static final long lockWaitTimeStamp = 1000 * 60 * 2;

    public JedisQueueStore(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    private String makePoolQueueKey(String queueID) {
        return queueID + jedisPoolSuffix;
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
        if (locked.get().incrementAndGet() > 1) {
            return true;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            String redisLockKey = makeRedisLockKey(queueID);
            long lockRequestTime = System.currentTimeMillis();
            while (true) {
                String result = jedis.set(redisLockKey, "lockTheQueue", "NX", "EX", 120);
                if (StringUtils.isNotEmpty(result) && result.equalsIgnoreCase("OK")) {
                    return true;
                }
                if (lockRequestTime + lockWaitTimeStamp > System.currentTimeMillis()) {
                    locked.get().decrementAndGet();
                    return false;
                }
                long sleepTime = jedis.ttl(redisLockKey) * 1000 - 10;
                if (sleepTime > lockRequestTime) {
                    return false;
                }
                if (sleepTime > 0) {
                    CommonUtil.sleep(sleepTime / 4);
                }
            }
        } finally {
            IOUtils.closeQuietly(jedis);
        }
    }

    private void unLockQueue(String queueID) {
        if (locked.get() == null) {
            return;
        }
        if (locked.get().decrementAndGet() > 0) {
            return;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.del(makeRedisLockKey(queueID));
            locked.remove();
        } finally {
            IOUtils.closeQuietly(jedis);
        }
    }

    @Override
    public long size(String queueID) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.hlen(makeDataKey(queueID));
        } finally {
            IOUtils.closeQuietly(jedis);
        }
    }

    @Override
    public boolean addFirst(String queueID, ResourceItem e) {
        Jedis jedis = null;
        if (!lockQueue(queueID)) {
            return false;
        }
        try {
            remove(queueID, e.getKey());
            jedis = jedisPool.getResource();
            jedis.lpush(makePoolQueueKey(queueID), e.getKey());
            jedis.hset(makeDataKey(queueID), e.getKey(), JSONObject.toJSONString(e));
        } finally {
            IOUtils.closeQuietly(jedis);
            unLockQueue(queueID);
        }
        return true;
    }

    @Override
    public boolean addLast(String queueID, ResourceItem e) {
        if (!lockQueue(queueID)) {
            return false;
        }
        remove(queueID, e.getKey());
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.rpush(makePoolQueueKey(queueID), e.getKey());
            jedis.hset(makeDataKey(queueID), e.getKey(), JSONObject.toJSONString(e));
        } finally {
            IOUtils.closeQuietly(jedis);
            unLockQueue(queueID);
        }
        return true;
    }

    @Override
    public boolean addIndex(String queueID, long index, ResourceItem e) {
        if (!lockQueue(queueID)) {
            return false;
        }
        remove(queueID, e.getKey());
        Jedis jedis = jedisPool.getResource();
        try {
            String poolQueueKey = makePoolQueueKey(queueID);
            Long length = jedis.llen(poolQueueKey);
            if (index <= length) {
                index = length - 1;
            }
            String position = jedis.lindex(makePoolQueueKey(queueID), index);
            if (isNil(position)) {
                jedis.rpush(poolQueueKey, e.getKey());
            } else {
                jedis.linsert(poolQueueKey, BinaryClient.LIST_POSITION.AFTER, position, e.getKey());
            }
            jedis.hset(makeDataKey(queueID), e.getKey(), JSONObject.toJSONString(e));
        } finally {
            IOUtils.closeQuietly(jedis);
            unLockQueue(queueID);
        }
        return true;
    }

    private boolean isNil(String input) {
        return StringUtils.isBlank(input) || StringUtils.equalsIgnoreCase(input, "nil");
    }

    @Override
    public ResourceItem poll(String queueID) {
        if (!lockQueue(queueID)) {
            return null;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            String firstResourceKey = jedis.lpop(makePoolQueueKey(queueID));
            if (isNil(firstResourceKey)) {
                return null;
            }
            String dataJson = jedis.hget(makeDataKey(queueID), firstResourceKey);
            if (isNil(dataJson)) {
                throw new IllegalStateException("this is no meta data for key queue :" + queueID + " ,for resourceKey :" + firstResourceKey);
            }
            jedis.hdel(makeDataKey(queueID), firstResourceKey);
            return JSONObject.toJavaObject(JSON.parseObject(dataJson), ResourceItem.class);
        } finally {
            IOUtils.closeQuietly(jedis);
            unLockQueue(queueID);
        }
    }


    @Override
    public ResourceItem get(String queueID, String key) {
        if (!lockQueue(queueID)) {
            return null;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            String dataJson = jedis.hget(makeDataKey(queueID), key);
            if (isNil(dataJson)) {
                return null;
            }
            return JSONObject.toJavaObject(JSON.parseObject(dataJson), ResourceItem.class);
        } finally {
            IOUtils.closeQuietly(jedis);
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
        Jedis jedis = jedisPool.getResource();
        try {
            List<String> queue = jedis.lrange(makePoolQueueKey(queueID), 0, -1);
            return queue.indexOf(key);
        } finally {
            IOUtils.closeQuietly(jedis);
            unLockQueue(queueID);
        }
    }

    @Override
    public boolean update(String queueID, ResourceItem e) {
        if (!lockQueue(queueID)) {
            return false;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            String dataJson = jedis.hget(makeDataKey(queueID), e.getKey());
            jedis.hset(makeDataKey(queueID), e.getKey(), JSONObject.toJSONString(e));
            return !isNil(dataJson);
        } finally {
            IOUtils.closeQuietly(jedis);
            unLockQueue(queueID);
        }
    }

    @Override
    public ResourceItem remove(String queueID, String key) {
        if (!lockQueue(queueID)) {
            return null;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            String dataJson = jedis.hget(makeDataKey(queueID), key);
            if (isNil(dataJson)) {
                return null;
            } else {
                jedis.hdel(makeDataKey(queueID), key);
                //lrem很消耗资源,尽量减少该命令操作
                jedis.lrem(makePoolQueueKey(queueID), 1, key);
            }
            return JSONObject.toJavaObject(JSON.parseObject(dataJson), ResourceItem.class);
        } finally {
            IOUtils.closeQuietly(jedis);
            unLockQueue(queueID);
        }
    }

    @Override
    public void addBatch(String queueID, Set<ResourceItem> resourceItems) {
        if (!lockQueue(queueID)) {
            return;
        }
        final Jedis jedis = jedisPool.getResource();
        final String dataKey = makeDataKey(queueID);
        try {
            final Set<String> hkeys = jedis.hkeys(dataKey);
            Set<ResourceItem> filterSet = Sets.filter(resourceItems, new Predicate<ResourceItem>() {
                @Override
                public boolean apply(ResourceItem input) {
                    return !hkeys.contains(input.getKey());
                }
            });

            String poolQueueKey = makePoolQueueKey(queueID);
            for (ResourceItem resourceItem : filterSet) {
                jedis.hset(dataKey, resourceItem.getKey(), JSONObject.toJSONString(resourceItem));
                jedis.rpush(poolQueueKey, resourceItem.getKey());
            }
        } finally {
            IOUtils.closeQuietly(jedis);
            unLockQueue(queueID);
        }
    }

    @Override
    public Set<String> notExisted(String queueID, Set<String> resourceItemKeys) {
        if (!lockQueue(queueID)) {
            return Collections.emptySet();
        }
        Jedis jedis = jedisPool.getResource();
        try {
            final Set<String> hkeys = jedis.hkeys(makeDataKey(queueID));
            return Sets.filter(resourceItemKeys, new Predicate<String>() {
                @Override
                public boolean apply(String input) {
                    return !hkeys.contains(input);
                }
            });
        } finally {
            IOUtils.closeQuietly(jedis);
            unLockQueue(queueID);
        }
    }

    @Override
    public void clear(String queueID) {
        lockQueue(queueID);
        Jedis jedis = jedisPool.getResource();
        try {
            String poolQueueKey = makePoolQueueKey(queueID);
            String dataKey = makeDataKey(queueID);
            jedis.del(poolQueueKey);
            jedis.del(dataKey);
        } finally {
            IOUtils.closeQuietly(jedis);
            unLockQueue(queueID);
        }
    }

    @Override
    public List<ResourceItem> queryAll(String queueID) {
        lockQueue(queueID);
        Jedis jedis = jedisPool.getResource();
        try {
            final Map<String, String> map = jedis.hgetAll(makeDataKey(queueID));
            return Lists.transform(jedis.lrange(makePoolQueueKey(queueID), 0, -1), new Function<String, ResourceItem>() {
                @Override
                public ResourceItem apply(String input) {
                    return JSONObject.toJavaObject(JSONObject.parseObject(map.get(input)), ResourceItem.class);
                }
            });
        } finally {
            IOUtils.closeQuietly(jedis);
            unLockQueue(queueID);
        }
    }
}
