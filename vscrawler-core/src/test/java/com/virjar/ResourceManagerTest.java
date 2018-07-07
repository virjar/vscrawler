package com.virjar;

import com.virjar.vscrawler.core.resourcemanager.ResourceManager;
import com.virjar.vscrawler.core.resourcemanager.ResourceManagerFactory;
import com.virjar.vscrawler.core.resourcemanager.model.AllResourceItems;
import com.virjar.vscrawler.core.resourcemanager.model.ResourceItem;
import com.virjar.vscrawler.core.resourcemanager.model.ResourceSetting;
import com.virjar.vscrawler.core.resourcemanager.service.RamQueueStore;
import com.virjar.vscrawler.core.resourcemanager.service.ResourceLoader;
import com.virjar.vscrawler.core.resourcemanager.service.ResourceQueue;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by virjar on 2018/7/6.
 */
public class ResourceManagerTest {
    private static final String tag = "testTag";

    public static void main(String[] args) {
        RamQueueStore ramQueueStore = new RamQueueStore();
        ResourceManager resourceManager = ResourceManagerFactory.create().registryResourceQueue(
                new ResourceQueue(tag, ramQueueStore, ResourceSetting.create().setLock(true).setLockForceLeaseDuration(100), new ResourceLoader() {
                    @Override
                    public boolean loadResource(Collection<ResourceItem> resourceItems) {
                        for (int i = 0; i < 100; i++) {
                            ResourceItem resourceItem = new ResourceItem();
                            resourceItem.setKey("key_" + i);
                            resourceItem.setData(resourceItem.getKey());
                            resourceItems.add(resourceItem);
                        }
                        return false;
                    }
                })).build();
        for (int i = 0; i < 100000; i++) {
            ResourceItem resourceItem = resourceManager.allocate(tag);
            String data = "null";
            if (resourceItem != null) {
                feedBackRandom(tag, resourceManager, resourceItem);
                data = resourceItem.getData();
            }
            System.out.println(data);

        }

        printQueueStatus(resourceManager, tag);

    }

    private static void feedBackRandom(String tag, ResourceManager resourceManager, ResourceItem resourceItem) {
        int i = ThreadLocalRandom.current().nextInt(10);
        if (i > 4) {
            return;
        }
        i = ThreadLocalRandom.current().nextInt(10);
        resourceManager.feedBack(tag, resourceItem.getKey(), i > 8);
    }

    private static void printQueueStatus(ResourceManager resourceManager, String tag) {
        AllResourceItems allResourceItems = resourceManager.queueStatus(tag);
        System.out.println(allResourceItems.getPollingQueue().size() + "  " + allResourceItems.getLeaveQueue().size() + "   " + allResourceItems.getForbiddenQueue().size());
    }
}
