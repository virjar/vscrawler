package com.virjar.vscrawler.resourcemanager.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import com.virjar.vscrawler.resourcemanager.model.ResourceItem;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Set;

/**
 * Created by virjar on 2018/1/7.<br/>
 *
 * @author virjar
 * @since 0.2.2
 */
public class JedisStoreQueue implements StoreQueue {
    //存放key的轮询数据
    private static final String jedisPoolSuffix = "_jedis_polling";
    //存放数据
    private static final String jedisDataSuffix = "_jedis_data";
    private JedisPool jedisPool;

    public JedisStoreQueue(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    private String makePoolQueueKey(String queueID) {
        return queueID + jedisPoolSuffix;
    }

    private String makeDataKey(String queueID) {
        return queueID + jedisDataSuffix;
    }

    @Override
    public long size(String queueID) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.llen(makePoolQueueKey(queueID));
        } finally {
            IOUtils.closeQuietly(jedis);
        }
    }

    @Override
    public boolean addFirst(String queueID, ResourceItem e) {
        remove(queueID, e.getKey());
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.lpush(makePoolQueueKey(queueID), e.getKey());
            jedis.hset(makeDataKey(queueID), e.getKey(), JSONObject.toJSONString(e));
        } finally {
            IOUtils.closeQuietly(jedis);
        }
        return true;
    }

    @Override
    public boolean addLast(String queueID, ResourceItem e) {
        remove(queueID, e.getKey());
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.rpush(makePoolQueueKey(queueID), e.getKey());
            jedis.hset(makeDataKey(queueID), e.getKey(), JSONObject.toJSONString(e));
        } finally {
            IOUtils.closeQuietly(jedis);
        }
        return true;
    }

    @Override
    public boolean addIndex(String queueID, long index, ResourceItem e) {
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

        }
        return true;
    }

    private boolean isNil(String input) {
        return StringUtils.equalsIgnoreCase(input, "nil") || StringUtils.isBlank(input);
    }

    @Override
    public ResourceItem poll(String queueID) {
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
            return JSONObject.toJavaObject(JSON.parseObject(dataJson), ResourceItem.class);
        } finally {
            IOUtils.closeQuietly(jedis);
        }
    }


    @Override
    public ResourceItem get(String queueID, String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            String dataJson = jedis.hget(makeDataKey(queueID), key);
            if (isNil(dataJson)) {
                return null;
            }
            return JSONObject.toJavaObject(JSON.parseObject(dataJson), ResourceItem.class);
        } finally {
            IOUtils.closeQuietly(jedis);
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
        Jedis jedis = jedisPool.getResource();
        try {
            List<String> queue = jedis.lrange(makePoolQueueKey(queueID), 0, -1);
            return queue.indexOf(key);
        } finally {
            IOUtils.closeQuietly(jedis);
        }
    }

    @Override
    public boolean update(String queueID, ResourceItem e) {
        Jedis jedis = jedisPool.getResource();
        try {
            String dataJson = jedis.hget(makeDataKey(queueID), e.getKey());
            jedis.hset(makeDataKey(queueID), e.getKey(), JSONObject.toJSONString(e));
            return !isNil(dataJson);
        } finally {
            IOUtils.closeQuietly(jedis);
        }
    }

    @Override
    public ResourceItem remove(String queueID, String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            String dataJson = jedis.hget(makeDataKey(queueID), key);
            jedis.hdel(makeDataKey(queueID), key);
            jedis.lrem(makePoolQueueKey(queueID), 1, key);
            if (isNil(dataJson)) {
                return null;
            }
            return JSONObject.toJavaObject(JSON.parseObject(dataJson), ResourceItem.class);
        } finally {
            IOUtils.closeQuietly(jedis);
        }
    }

    @Override
    public void addBatch(String queueID, Set<ResourceItem> resourceItems) {
        final Jedis jedis = jedisPool.getResource();
        final String dataKey = makeDataKey(queueID);
        String poolQueueKey = makePoolQueueKey(queueID);
        try {
            Set<ResourceItem> filterSet = Sets.filter(resourceItems, new Predicate<ResourceItem>() {
                @Override
                public boolean apply(ResourceItem input) {
                    return isNil(jedis.hget(dataKey, input.getKey()));
                }
            });
            for (ResourceItem resourceItem : filterSet) {
                jedis.hset(dataKey, resourceItem.getKey(), JSONObject.toJSONString(resourceItem));
                jedis.rpush(poolQueueKey, resourceItem.getKey());
            }
        } finally {
            IOUtils.closeQuietly(jedis);
        }
    }
}
