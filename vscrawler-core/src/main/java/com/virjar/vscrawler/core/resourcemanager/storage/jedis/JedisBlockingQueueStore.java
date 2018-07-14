package com.virjar.vscrawler.core.resourcemanager.storage.jedis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.virjar.vscrawler.core.resourcemanager.model.ResourceItem;
import com.virjar.vscrawler.core.resourcemanager.storage.BlockingQueueStore;
import org.apache.commons.io.IOUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Set;

/**
 * Created by virjar on 2018/7/14.<br>
 * blocking queue implemented by redis client
 *
 * @author virjar
 * @since 0.3.2
 */
public class JedisBlockingQueueStore extends BaseJedisOperationQueueStore implements BlockingQueueStore {

    //存放key的轮询数据
    private static final String jedisPoolSuffix = "_jedis_polling";
    //存放数据
    private static final String jedisDataSuffix = "_jedis_data";

    private String makePoolQueueKey(String queueID) {
        return queueID + jedisPoolSuffix;
    }

    private String makeDataKey(String queueID) {
        return queueID + jedisDataSuffix;
    }

    public JedisBlockingQueueStore(JedisPool jedisPool) {
        super(jedisPool);
    }

    @Override
    public void zadd(String queueID, ResourceItem resourceItem) {
        if (!lockQueue(queueID)) {
            throw new RuntimeException("failed to acquire resource queue for queueID: " + queueID);
        }
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.zadd(makePoolQueueKey(queueID), resourceItem.getValidTimeStamp(), resourceItem.getKey());
            jedis.hset(makeDataKey(queueID), resourceItem.getKey(), JSONObject.toJSONString(resourceItem));
        } finally {
            IOUtils.closeQuietly(jedis);
            unLockQueue(queueID);
        }
    }

    @Override
    public long size(String queueID) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.zcard(makePoolQueueKey(queueID));
        } finally {
            IOUtils.closeQuietly(jedis);
        }
    }

    @Override
    public ResourceItem poll(String queueID) {
        Jedis jedis = jedisPool.getResource();
        try {
            Set<String> zrange = jedis.zrange(makePoolQueueKey(queueID), 0, 1);
            if (zrange.size() == 0) {
                return null;
            }
            return JSONObject.toJavaObject(JSON.parseObject(zrange.iterator().next()), ResourceItem.class);
        } finally {
            IOUtils.closeQuietly(jedis);
        }
    }

    @Override
    public ResourceItem pop(String queueID) {
        if (!lockQueue(queueID)) {
            throw new RuntimeException("failed to acquire resource queue for queueID: " + queueID);
        }
        Jedis jedis = jedisPool.getResource();
        try {
            ResourceItem resourceItem = poll(queueID);
            if (resourceItem == null) {
                return null;
            }
            jedis.zrem(makePoolQueueKey(queueID), resourceItem.getKey());
            return resourceItem;
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
            String dataKey = makeDataKey(queueID);
            String poolQueueKey = makePoolQueueKey(queueID);
            jedis.del(poolQueueKey);
            jedis.del(dataKey);
        } finally {
            IOUtils.closeQuietly(jedis);
            unLockQueue(queueID);
        }
    }

    @Override
    public List<ResourceItem> queryAll(String queueID) {
        return null;
    }
}
