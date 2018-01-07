package com.virjar.vscrawler.resourcemanager.service;

import com.google.common.base.Preconditions;
import com.virjar.vscrawler.resourcemanager.model.ResourceItem;
import com.virjar.vscrawler.resourcemanager.model.ResourceSetting;
import com.virjar.vscrawler.resourcemanager.util.CatchRegexPattern;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by virjar on 2018/1/4.
 */
public class ResourceQueue {
    private String tag;
    private static final String polling = "vscrawler_resourceManager_polling_";
    private static final String leave = "vscrawler_resourceManager_leave_";
    private ScoreableQueue<ResourceItem> pollingQueue;
    private ScoreableQueue<ResourceItem> leaveQueue;
    private ResourceSetting resourceSetting;

    public ResourceQueue(String tag) {
        Preconditions.checkArgument(CatchRegexPattern.compile("[a-zA-Z_]").matcher(tag).matches());
        this.tag = tag;
    }

    private String makePollingQueueID() {
        return polling + tag;
    }

    private String makeLeaveQueueID() {
        return leave + tag;
    }

    private int recoveryFromLeaveQueue() {
        ResourceItem resourceItem = leaveQueue.poll(makeLeaveQueueID());
        if (resourceItem == null) {
            return 0;
        }
        int recoveryItemsSize = 0;
        String headerKey = resourceItem.getKey();
        do {
            if (resourceItem.getValidTimeStamp() < System.currentTimeMillis()) {
                recoveryItemsSize++;
                pollingQueue.addLast(makePollingQueueID(), resourceItem);
            } else {
                leaveQueue.addLast(makeLeaveQueueID(), resourceItem);
            }
            resourceItem = leaveQueue.poll(makeLeaveQueueID());
        } while (resourceItem != null && !StringUtils.equals(headerKey, resourceItem.getKey()));
        return recoveryItemsSize;
    }

    /**
     * 得到一个资源
     *
     * @return 资源管理器当前分发的资源, 如果系统不能分发资源, 则该返回可能为null
     */
    public ResourceItem allocate() {
        while (true) {
            ResourceItem resourceItem = pollingQueue.poll(makePollingQueueID());
            if (resourceItem == null && recoveryFromLeaveQueue() > 0) {
                resourceItem = pollingQueue.poll(makePollingQueueID());
            }
            if (resourceItem == null) {
                //can not get available resource
                return null;
            }
            //check
            if (resourceItem.getValidTimeStamp() > 0 && resourceItem.getValidTimeStamp() > System.currentTimeMillis()) {
                leaveQueue.addLast(makeLeaveQueueID(), resourceItem);
                continue;
            }

            //check 通过,设置更新状态参数
            if (resourceSetting.isLock()) {
                resourceItem.setValidTimeStamp(System.currentTimeMillis() + resourceSetting.getLockForceLeaseDuration());
            }

            long queueSize = pollingQueue.size(makePollingQueueID());
            if (queueSize <= 3) {
                pollingQueue.addLast(makePollingQueueID(), resourceItem);
            }
            long index = (long) (resourceSetting.getScoreRatio() * queueSize);
            if (index > queueSize - 1) {
                index = queueSize - 1;
            }
            pollingQueue.addIndex(makePollingQueueID(), index, resourceItem);
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

    }

    /**
     * 永久封禁某个资源
     *
     * @param key 每个资源都应该有一个key
     */
    public void forbidden(String key) {

    }

    /**
     * 解除永久封禁
     *
     * @param key 每个资源都应该有一个key
     */
    public void unForbidden(String key) {

    }
}
