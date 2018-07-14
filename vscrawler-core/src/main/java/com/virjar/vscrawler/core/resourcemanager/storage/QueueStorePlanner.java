package com.virjar.vscrawler.core.resourcemanager.storage;

/**
 * Created by virjar on 2018/7/14.
 *
 * @author virjar
 * @since 0.3.2
 */
public interface QueueStorePlanner {
    ScoredQueueStore getScoredQueueStore();

    ForbiddenQueueStore getForbiddenQueueStore();

    BlockingQueueStore getBlockingQueueStore();
}
