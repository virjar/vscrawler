package com.virjar.vscrawler.core.resourcemanager.storage.jedis;

import com.google.common.base.Preconditions;
import com.virjar.vscrawler.core.resourcemanager.storage.BlockingQueueStore;
import com.virjar.vscrawler.core.resourcemanager.storage.ForbiddenQueueStore;
import com.virjar.vscrawler.core.resourcemanager.storage.QueueStorePlanner;
import com.virjar.vscrawler.core.resourcemanager.storage.ScoredQueueStore;
import redis.clients.jedis.JedisPool;

/**
 * Created by virjar on 2018/7/14.<br>
 *
 * @author virjar
 * @since 0.3.2
 */
public class JedisQueueStorePlanner implements QueueStorePlanner {
    private JedisPool jedisPool;

    private ScoredQueueStore scoredQueueStore = null;
    private ForbiddenQueueStore forbiddenQueueStore = null;
    private BlockingQueueStore blockingQueueStore = null;

    public JedisQueueStorePlanner(JedisPool jedisPool) {
        Preconditions.checkNotNull(jedisPool, "jedisPool can not be null");
        this.jedisPool = jedisPool;
    }

    @Override
    public ScoredQueueStore getScoredQueueStore() {
        if (scoredQueueStore == null) {
            scoredQueueStore = new JedisScoredQueueStore(jedisPool);
        }
        return scoredQueueStore;
    }

    @Override
    public ForbiddenQueueStore getForbiddenQueueStore() {
        if (forbiddenQueueStore == null) {
            forbiddenQueueStore = new JedisForbiddenQueueStore(jedisPool);
        }
        return forbiddenQueueStore;
    }

    @Override
    public BlockingQueueStore getBlockingQueueStore() {
        if (blockingQueueStore == null) {
            blockingQueueStore = new JedisBlockingQueueStore(jedisPool);
        }
        return blockingQueueStore;
    }
}
