package com.virjar.vscrawler.core.resourcemanager.storage.jedis;

import com.virjar.dungproxy.client.util.CommonUtil;
import com.virjar.vscrawler.core.monitor.VSCrawlerMonitor;
import org.apache.commons.io.IOUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by virjar on 2018/7/14.<br>
 * lock support for Jedis operation
 */
public abstract class BaseJedisOperationQueueStore {
    private static final String jedisLockKeySuffix = "_vscrawler_resourceManager_queue_lock";

    private static final long lockWaitTimeStamp = 1000 * 60 * 2;
    JedisPool jedisPool;
    private InheritableThreadLocal<AtomicInteger> locked = new InheritableThreadLocal<>();

    BaseJedisOperationQueueStore(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    private String makeRedisLockKey(String queueID) {
        return queueID + jedisLockKeySuffix;
    }

    boolean lockQueue(String queueID) {
        boolean result = lockQueueInternal(queueID);
        if (!result) {
            VSCrawlerMonitor.recordOne("acquire_resource_queue_failed");
            VSCrawlerMonitor.recordOne("acquire_resource_queue_failed_" + queueID);
        }
        return result;
    }

    private boolean lockQueueInternal(String queueID) {
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
                if (result.equalsIgnoreCase("OK")) {
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

    void unLockQueue(String queueID) {
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
}
