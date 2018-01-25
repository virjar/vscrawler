package com.virjar.vscrawler.core.resourcemanager.service;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.virjar.vscrawler.core.resourcemanager.model.AllResourceItems;
import com.virjar.vscrawler.core.resourcemanager.model.ResourceItem;
import com.virjar.vscrawler.core.resourcemanager.model.ResourceSetting;
import com.virjar.vscrawler.core.util.CatchRegexPattern;
import lombok.Getter;
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
public class ResourceQueue {
    @Getter
    private String tag;
    private static final String polling = "vscrawler_resourceManager_polling_";
    private static final String leave = "vscrawler_resourceManager_leave_";
    private static final String forbidden = "vscrawler_resourceManager_forbidden_";
    private QueueStore queue;
    private ResourceSetting resourceSetting;
    private static final long nextCheckLeaveQueueDuration = 1000 * 60 * 30;
    private long nextCheckLeaveQueue = System.currentTimeMillis() + nextCheckLeaveQueueDuration;
    private ResourceLoader resourceLoader;

    public ResourceQueue(String tag, QueueStore queue, ResourceSetting resourceSetting, ResourceLoader resourceLoader) {
        Preconditions.checkArgument(CatchRegexPattern.compile("[a-zA-Z0-9_]+").matcher(tag).matches(), "tag pattern must be \"[a-zA-Z_]+\"");
        this.tag = tag;
        this.queue = queue;
        this.resourceSetting = resourceSetting;
        this.resourceLoader = resourceLoader;
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
        resourceKeys = queue.notExisted(makePollingQueueID(), resourceKeys);
        resourceKeys = queue.notExisted(makeLeaveQueueID(), resourceKeys);
        resourceKeys = queue.notExisted(makeForbiddenQueueID(), resourceKeys);
        final Set<String> canImportKey = resourceKeys;
        queue.addBatch(makePollingQueueID(), Sets.filter(resourceItems, new Predicate<ResourceItem>() {
            @Override
            public boolean apply(ResourceItem input) {
                return canImportKey.contains(input.getKey());
            }
        }));
    }

    public void reloadResource() {
        queue.clear(makeForbiddenQueueID());
        queue.clear(makePollingQueueID());
        queue.clear(makeLeaveQueueID());
        loadResource();
    }

    public void loadResource() {
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

    private int recoveryFromLeaveQueue() {
        ResourceItem resourceItem = queue.poll(makeLeaveQueueID());
        if (resourceItem == null) {
            return 0;
        }
        int recoveryItemsSize = 0;
        String headerKey = resourceItem.getKey();
        do {
            if (resourceItem.getValidTimeStamp() < System.currentTimeMillis()) {
                recoveryItemsSize++;
                queue.addFirst(makePollingQueueID(), resourceItem);
            } else {
                queue.addLast(makeLeaveQueueID(), resourceItem);
            }
            resourceItem = queue.poll(makeLeaveQueueID());
        } while (resourceItem != null && !StringUtils.equals(headerKey, resourceItem.getKey()));
        nextCheckLeaveQueue = System.currentTimeMillis() + nextCheckLeaveQueueDuration;
        if (resourceItem != null) {
            queue.addFirst(makePollingQueueID(), resourceItem);
        }
        return recoveryItemsSize;
    }

    /**
     * 得到一个资源
     *
     * @return 资源管理器当前分发的资源, 如果系统不能分发资源, 则该返回可能为null
     */
    public ResourceItem allocate() {
        while (true) {
            ResourceItem resourceItem = queue.poll(makePollingQueueID());
            if (resourceItem == null || nextCheckLeaveQueue < System.currentTimeMillis()) {
                int recoverySize = recoveryFromLeaveQueue();
                if (recoverySize > 0 && resourceItem == null) {
                    resourceItem = queue.poll(makePollingQueueID());
                }
            }

            if (resourceItem == null && queue.size(makePollingQueueID()) == 0 && queue.size(makeLeaveQueueID()) == 0 && queue.size(makeForbiddenQueueID()) == 0) {
                loadResource();
                resourceItem = queue.poll(makePollingQueueID());
            }

            if (resourceItem == null) {
                //can not get available resource
                return null;
            }
            //check
            if (resourceItem.getValidTimeStamp() > System.currentTimeMillis()) {
                queue.addLast(makeLeaveQueueID(), resourceItem);
                continue;
            }

            //check 通过,设置更新状态参数
            if (resourceSetting.isLock()) {
                resourceItem.setValidTimeStamp(System.currentTimeMillis() + resourceSetting.getLockForceLeaseDuration());
            }

            long queueSize = queue.size(makePollingQueueID());
            if (queueSize <= 3) {
                queue.addLast(makePollingQueueID(), resourceItem);
            } else {
                long index = (long) (resourceSetting.getScoreRatio() * queueSize);
                if (index > queueSize - 1) {
                    index = queueSize - 1;
                }
                queue.addIndex(makePollingQueueID(), index, resourceItem);
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
        ResourceItem resourceItem = queue.get(makePollingQueueID(), key);
        if (resourceItem == null) {
            resourceItem = queue.get(makeLeaveQueueID(), key);
            inLeaveQueue = true;
        }
        if (resourceItem == null) {
            //can not find resource for key,for resource always forbidden
            return;
        }

        double newScore = resourceItem.getScore() * (resourceSetting.getScoreFactory() - 1) + (isOK ? 1 : 0);
        resourceItem.setScore(newScore);
        resourceItem.setValidTimeStamp(0);
        resourceItem.setKey(key);

        if (inLeaveQueue) {
            queue.remove(makeLeaveQueueID(), key);
            queue.addFirst(makePollingQueueID(), resourceItem);
            return;
        }
        if (isOK) {
            queue.update(makePollingQueueID(), resourceItem);
            return;
        }

        long queueSize = queue.size(makePollingQueueID()) - 1;
        long index = (long) ((queueSize) * (resourceSetting.getScoreRatio() + (1 - resourceSetting.getScoreRatio()) * (1 - resourceItem.getScore())));

        queue.remove(makePollingQueueID(), key);
        boolean addSuccess = false;
        try {
            addSuccess = queue.addIndex(makePollingQueueID(), index, resourceItem);
        } catch (Exception e) {
            //ignore
            //TODO log
        }
        if (!addSuccess) {
            queue.addLast(makePollingQueueID(), resourceItem);
        }
    }

    /**
     * 永久封禁某个资源
     *
     * @param key 每个资源都应该有一个key
     */
    public void forbidden(String key) {
        ResourceItem resourceItem = queue.remove(makePollingQueueID(), key);
        if (resourceItem != null) {
            queue.addLast(makeForbiddenQueueID(), resourceItem);
        }
        resourceItem = queue.remove(makeLeaveQueueID(), key);
        if (resourceItem != null) {
            queue.addLast(makeForbiddenQueueID(), resourceItem);
        }
    }

    /**
     * 解除永久封禁
     *
     * @param key 每个资源都应该有一个key
     */
    public void unForbidden(String key) {
        ResourceItem resourceItem = queue.get(makeForbiddenQueueID(), key);
        if (resourceItem == null) {
            //TODO log
            return;
        }
        resourceItem.setScore(0.5);
        queue.addFirst(makePollingQueueID(), resourceItem);
    }

    public void updateResource(ResourceItem resourceItem) {
        String key = resourceItem.getKey();
        ResourceItem resourceItem1 = queue.get(makePollingQueueID(), key);
        if (resourceItem1 != null) {
            resourceItem1.setData(resourceItem.getData());
            queue.update(makePollingQueueID(), resourceItem1);
            return;
        }

        resourceItem1 = queue.get(makeLeaveQueueID(), key);
        if (resourceItem1 != null) {
            resourceItem1.setData(resourceItem.getData());
            queue.update(makePollingQueueID(), resourceItem1);
            return;
        }

        resourceItem1 = queue.get(makeForbiddenQueueID(), key);
        if (resourceItem1 != null) {
            resourceItem1.setData(resourceItem.getData());
            queue.update(makeForbiddenQueueID(), resourceItem1);
        }
    }

    public AllResourceItems allResource() {
        return new AllResourceItems(queue.queryAll(makePollingQueueID()), queue.queryAll(makeLeaveQueueID()), queue.queryAll(makeForbiddenQueueID()));
    }
}
