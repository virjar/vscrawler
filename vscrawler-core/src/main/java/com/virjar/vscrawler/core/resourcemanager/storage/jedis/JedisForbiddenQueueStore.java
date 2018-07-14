package com.virjar.vscrawler.core.resourcemanager.storage.jedis;

import com.alibaba.fastjson.JSONObject;
import com.virjar.vscrawler.core.resourcemanager.model.ResourceItem;
import com.virjar.vscrawler.core.resourcemanager.storage.ForbiddenQueueStore;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

/**
 * Created by virjar on 2018/7/14.<br>
 *
 * @author virjar
 * @since 0.3.2
 */
public class JedisForbiddenQueueStore extends BaseJedisOperationQueueStore implements ForbiddenQueueStore {

    //存放数据
    private static final String jedisDataSuffix = "_jedis_data";

    public JedisForbiddenQueueStore(JedisPool jedisPool) {
        super(jedisPool);
    }

    private String makeDataKey(String queueID) {
        return queueID + jedisDataSuffix;
    }

    @Override
    public void add(String queueID, ResourceItem resourceItem) {
        if (!lockQueue(queueID)) {
            throw new RuntimeException("failed to acquire redis lock");
        }
        Jedis jedis = jedisPool.getResource();
        try {
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
            return jedis.hlen(makeDataKey(queueID));
        } finally {
            IOUtils.closeQuietly(jedis);
        }
    }

    @Override
    public ResourceItem remove(String queueID, String key) {
        if (!lockQueue(queueID)) {
            throw new RuntimeException("failed to acquire redis lock");
        }
        Jedis jedis = jedisPool.getResource();
        try {
            String data = jedis.hget(makeDataKey(queueID), key);
            if (StringUtils.isBlank(data)) {
                return null;
            }
            jedis.hdel(makeDataKey(queueID), key);
            return JSONObject.toJavaObject(JSONObject.parseObject(data), ResourceItem.class);
        } finally {
            IOUtils.closeQuietly(jedis);
            unLockQueue(queueID);
        }
    }

    @Override
    public void clear(String queueID) {
        if (!lockQueue(queueID)) {
            throw new RuntimeException("failed to acquire redis lock");
        }
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.del(makeDataKey(queueID));
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
