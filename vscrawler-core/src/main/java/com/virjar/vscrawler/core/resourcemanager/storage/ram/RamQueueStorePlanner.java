package com.virjar.vscrawler.core.resourcemanager.storage.ram;

import com.virjar.vscrawler.core.resourcemanager.storage.BlockingQueueStore;
import com.virjar.vscrawler.core.resourcemanager.storage.ForbiddenQueueStore;
import com.virjar.vscrawler.core.resourcemanager.storage.QueueStorePlanner;
import com.virjar.vscrawler.core.resourcemanager.storage.ScoredQueueStore;

/**
 * Created by virjar on 2018/7/14.<br>
 *
 * @author virjar
 * @since 0.3.2
 */
public class RamQueueStorePlanner implements QueueStorePlanner {
    private ScoredQueueStore ramScoredQueueStore = new RamScoredQueueStore();
    private ForbiddenQueueStore forbiddenQueueStore = new RamForbiddenQueueStore();
    private BlockingQueueStore blockingQueueStore = new RamBlockingQueueStore();

    @Override
    public ScoredQueueStore getScoredQueueStore() {
        return ramScoredQueueStore;
    }

    @Override
    public ForbiddenQueueStore getForbiddenQueueStore() {
        return forbiddenQueueStore;
    }

    @Override
    public BlockingQueueStore getBlockingQueueStore() {
        return blockingQueueStore;
    }

    public static RamQueueStorePlanner instance = new RamQueueStorePlanner();
}
