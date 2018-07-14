package com.virjar.vscrawler.core.resourcemanager;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.virjar.vscrawler.core.monitor.MetricCollectorTask;
import com.virjar.vscrawler.core.monitor.VSCrawlerMonitor;
import com.virjar.vscrawler.core.resourcemanager.model.AllResourceItems;
import com.virjar.vscrawler.core.resourcemanager.model.ResourceItem;
import com.virjar.vscrawler.core.resourcemanager.model.ResourceSetting;
import com.virjar.vscrawler.core.resourcemanager.service.CombineResourceLoader;
import com.virjar.vscrawler.core.resourcemanager.service.ResourceLoader;
import com.virjar.vscrawler.core.resourcemanager.storage.BlockingQueueStore;
import com.virjar.vscrawler.core.resourcemanager.storage.ForbiddenQueueStore;
import com.virjar.vscrawler.core.resourcemanager.storage.QueueStorePlanner;
import com.virjar.vscrawler.core.resourcemanager.storage.ScoredQueueStore;
import com.virjar.vscrawler.core.util.CatchRegexPattern;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Set;

/**
 * Created by virjar on 2018/1/4.<br/>
 * 负责资源顺序控制,资源分值计算,资源封禁和解封,不负责资源存储
 *
 * @author virjar
 * @since 0.2.2
 */
@Slf4j
public class ResourceQueue {
    private static final String resourceQueueMonitorTag = "vscrawler_resource_";
    @Getter
    private String tag;
    private static final String polling = "vscrawler_resourceManager_polling_v2_";
    private static final String leave = "vscrawler_resourceManager_leave_v2_";
    private static final String forbidden = "vscrawler_resourceManager_forbidden_v2_";
    private ScoredQueueStore scoredQueueStore;
    private BlockingQueueStore blockingQueueStore;
    private ForbiddenQueueStore forbiddenQueueStore;

    private ResourceSetting resourceSetting;

    private ResourceLoader resourceLoader;

    public ResourceQueue(String tag, QueueStorePlanner queueStorePlanner, ResourceSetting resourceSetting, ResourceLoader resourceLoader) {
        this(tag, queueStorePlanner.getScoredQueueStore(), queueStorePlanner.getBlockingQueueStore(), queueStorePlanner.getForbiddenQueueStore(), resourceSetting, resourceLoader);
    }

    public ResourceQueue(final String tag,
                         ScoredQueueStore scoredQueueStore,
                         BlockingQueueStore blockingQueueStore,
                         ForbiddenQueueStore forbiddenQueueStore,
                         ResourceSetting resourceSetting, ResourceLoader resourceLoader) {
        Preconditions.checkArgument(CatchRegexPattern.compile("[a-zA-Z0-9_]+").matcher(tag).matches(), "tag pattern must be \"[a-zA-Z_]+\"");
        this.tag = tag;
        this.scoredQueueStore = scoredQueueStore;
        this.blockingQueueStore = blockingQueueStore;
        this.forbiddenQueueStore = forbiddenQueueStore;
        this.resourceSetting = resourceSetting;
        this.resourceLoader = resourceLoader;
        MetricCollectorTask.MetricCollector metricCollector = new MetricCollectorTask.MetricCollector() {
            @Override
            public void doCollect() {
                VSCrawlerMonitor.recordSize(resourceQueueMonitorTag + tag + "pollingQueueSize", ResourceQueue.this.scoredQueueStore.size(makePollingQueueID()));
                VSCrawlerMonitor.recordSize(resourceQueueMonitorTag + tag + "leaveQueueSize", ResourceQueue.this.blockingQueueStore.size(makeLeaveQueueID()));
                VSCrawlerMonitor.recordSize(resourceQueueMonitorTag + tag + "forbiddenQueueSize", ResourceQueue.this.forbiddenQueueStore.size(makeForbiddenQueueID()));
            }
        };
        MetricCollectorTask.register(metricCollector);
    }


    public void addResourceLoader(ResourceLoader resourceLoader) {
        ResourceLoader oldResourceLoader = this.resourceLoader;
        if (!(this.resourceLoader instanceof CombineResourceLoader)) {
            List<ResourceLoader> resourceLoaderList = Lists.newArrayList(oldResourceLoader, resourceLoader);
            this.resourceLoader = new CombineResourceLoader(resourceLoaderList);
        } else {
            CombineResourceLoader combineResourceLoader = (CombineResourceLoader) this.resourceLoader;
            combineResourceLoader.addNewResourceLoader(resourceLoader);
        }
    }

    private void importNewData(Set<ResourceItem> resourceItems) {
        Set<String> resourceKeys = Sets.newHashSet(Iterables.transform(resourceItems, new Function<ResourceItem, String>() {
            @Override
            public String apply(ResourceItem input) {
                input.setTag(tag);
                if (StringUtils.isBlank(input.getKey())) {
                    input.setKey(input.getData());
                }
                input.setScore(0.5);
                return input.getKey();
            }
        }));
        resourceKeys = scoredQueueStore.notExisted(makePollingQueueID(), resourceKeys);
        resourceKeys = blockingQueueStore.notExisted(makeLeaveQueueID(), resourceKeys);
        resourceKeys = forbiddenQueueStore.notExisted(makeForbiddenQueueID(), resourceKeys);
        final Set<String> canImportKey = resourceKeys;
        scoredQueueStore.addBatch(makePollingQueueID(), Sets.filter(resourceItems, new Predicate<ResourceItem>() {
            @Override
            public boolean apply(ResourceItem input) {
                return canImportKey.contains(input.getKey());
            }
        }));
    }

    public void reloadResource() {
        scoredQueueStore.clear(makeForbiddenQueueID());
        scoredQueueStore.clear(makePollingQueueID());
        scoredQueueStore.clear(makeLeaveQueueID());
        loadResource();
    }

    public void loadResource() {
        VSCrawlerMonitor.recordOne(resourceQueueMonitorTag + tag + "_load_resource");
        Set<ResourceItem> collection = Sets.newHashSet();
        boolean hasNext = resourceLoader.loadResource(collection);
        importNewData(collection);
        while (hasNext) {
            collection.clear();
            hasNext = resourceLoader.loadResource(collection);
            importNewData(collection);
        }
    }

    private String makeForbiddenQueueID() {
        return forbidden + tag;
    }

    private String makePollingQueueID() {
        return polling + tag;
    }

    private String makeLeaveQueueID() {
        return leave + tag;
    }


    /**
     * 得到一个资源
     *
     * @return 资源管理器当前分发的资源, 如果系统不能分发资源, 则该返回可能为null
     */
    public ResourceItem allocate() {
        boolean hasQueryBlockingQueue = false;
        while (true) {
            ResourceItem resourceItem = scoredQueueStore.pop(makePollingQueueID());
            if (resourceItem == null && !hasQueryBlockingQueue) {
                resourceItem = blockingQueueStore.pop(makeLeaveQueueID());
                hasQueryBlockingQueue = true;
            }

            if (resourceItem == null && scoredQueueStore.size(makePollingQueueID()) == 0 && blockingQueueStore.size(makeLeaveQueueID()) == 0 && forbiddenQueueStore.size(makeForbiddenQueueID()) == 0) {
                loadResource();
                resourceItem = scoredQueueStore.pop(makePollingQueueID());
            }

            if (resourceItem == null) {
                //can not get available resource
                return null;
            }

            //check 该资源在未来才会生效,放置到blockingQueue
            if (resourceItem.getValidTimeStamp() > System.currentTimeMillis()) {
                blockingQueueStore.zadd(makeLeaveQueueID(), resourceItem);
                continue;
            }

            //check 通过,设置更新状态参数
            if (resourceSetting.isLock()) {
                resourceItem.setValidTimeStamp(System.currentTimeMillis() + resourceSetting.getLockForceLeaseDuration());
            }

            long queueSize = scoredQueueStore.size(makePollingQueueID());
            if (queueSize <= 3) {
                scoredQueueStore.addLast(makePollingQueueID(), resourceItem);
            } else {
                long index = (long) (resourceSetting.getScoreRatio() * queueSize);
                if (index > queueSize - 1) {
                    index = queueSize - 1;
                }
                scoredQueueStore.addIndex(makePollingQueueID(), index, resourceItem);
            }
            return resourceItem;
        }
    }

    /**
     * 反馈某个资源的使用状况
     *
     * @param key  每个资源都应该有一个key
     * @param isOK 该资源状态,可用还是不可用
     */
    public void feedBack(String key, boolean isOK) {
        boolean inLeaveQueue = false;
        ResourceItem resourceItem = scoredQueueStore.get(makePollingQueueID(), key);
        if (resourceItem == null) {
            resourceItem = blockingQueueStore.get(makeLeaveQueueID(), key);
            inLeaveQueue = true;
        }
        if (resourceItem == null) {
            VSCrawlerMonitor.recordOne(resourceQueueMonitorTag + "feedback_findKey_failed");
            //can not find resource for key,for resource always forbidden
            return;
        }

        double newScore = resourceItem.getScore() * (resourceSetting.getScoreFactory() - 1) + (isOK ? 1 : 0);
        resourceItem.setScore(newScore);
        resourceItem.setValidTimeStamp(0);
        resourceItem.setKey(key);

        if (inLeaveQueue) {
            forbiddenQueueStore.remove(makeLeaveQueueID(), key);
            scoredQueueStore.addFirst(makePollingQueueID(), resourceItem);
            return;
        }
        if (isOK) {
            scoredQueueStore.update(makePollingQueueID(), resourceItem);
            return;
        }

        long queueSize = scoredQueueStore.size(makePollingQueueID()) - 1;
        long index = (long) ((queueSize) * (resourceSetting.getScoreRatio() + (1 - resourceSetting.getScoreRatio()) * (1 - resourceItem.getScore())));

        scoredQueueStore.remove(makePollingQueueID(), key);
        boolean addSuccess = false;
        try {
            addSuccess = scoredQueueStore.addIndex(makePollingQueueID(), index, resourceItem);
        } catch (Exception e) {
            VSCrawlerMonitor.recordOne(resourceQueueMonitorTag + tag + "relocate_resource_sequence_failed");
            log.error("relocate resource sequence failed", e);
        }
        if (!addSuccess) {
            scoredQueueStore.addLast(makePollingQueueID(), resourceItem);
        }
    }

    /**
     * 永久封禁某个资源
     *
     * @param key 每个资源都应该有一个key
     */
    public void forbidden(String key) {
        ResourceItem resourceItem = scoredQueueStore.remove(makePollingQueueID(), key);
        if (resourceItem != null) {
            forbiddenQueueStore.add(makeForbiddenQueueID(), resourceItem);
            return;
        }
        resourceItem = blockingQueueStore.remove(makeLeaveQueueID(), key);
        if (resourceItem != null) {
            forbiddenQueueStore.add(makeForbiddenQueueID(), resourceItem);
        }
    }

    /**
     * 解除永久封禁
     *
     * @param key 每个资源都应该有一个key
     */
    public void unForbidden(String key) {
        ResourceItem resourceItem = forbiddenQueueStore.get(makeForbiddenQueueID(), key);
        if (resourceItem == null) {
            VSCrawlerMonitor.recordOne(resourceQueueMonitorTag + tag + "_find_resource_meta_failed");
            log.warn("can not find resource meta data for tag:{} key:{}", tag, key);
            return;
        }
        resourceItem.setScore(0.5);
        scoredQueueStore.addFirst(makePollingQueueID(), resourceItem);
    }

    public void updateResource(ResourceItem resourceItem) {
        String key = resourceItem.getKey();
        ResourceItem resourceItem1 = scoredQueueStore.get(makePollingQueueID(), key);
        if (resourceItem1 != null) {
            resourceItem1.setData(resourceItem.getData());
            scoredQueueStore.update(makePollingQueueID(), resourceItem1);
            return;
        }

        resourceItem1 = blockingQueueStore.get(makeLeaveQueueID(), key);
        if (resourceItem1 != null) {
            resourceItem1.setData(resourceItem.getData());
            blockingQueueStore.update(makePollingQueueID(), resourceItem1);
            return;
        }

        resourceItem1 = forbiddenQueueStore.get(makeForbiddenQueueID(), key);
        if (resourceItem1 != null) {
            resourceItem1.setData(resourceItem.getData());
            forbiddenQueueStore.update(makeForbiddenQueueID(), resourceItem1);
        }
    }

    public AllResourceItems allResource() {
        return new AllResourceItems(scoredQueueStore.queryAll(makePollingQueueID()), blockingQueueStore.queryAll(makeLeaveQueueID()), forbiddenQueueStore.queryAll(makeForbiddenQueueID()));
    }
}
