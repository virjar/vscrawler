package com.virjar;

import com.virjar.vscrawler.core.resourcemanager.ResourceManager;
import com.virjar.vscrawler.core.resourcemanager.ResourceManagerFactory;
import com.virjar.vscrawler.core.resourcemanager.ResourceQueue;
import com.virjar.vscrawler.core.resourcemanager.model.AllResourceItems;
import com.virjar.vscrawler.core.resourcemanager.model.ResourceItem;
import com.virjar.vscrawler.core.resourcemanager.model.ResourceSetting;
import com.virjar.vscrawler.core.resourcemanager.service.ResourceLoader;
import com.virjar.vscrawler.core.resourcemanager.storage.ram.RamQueueStorePlanner;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by virjar on 2018/7/6.
 */
public class ResourceManagerTest {
    private static final String tag = "testTag";

    public static void main(String[] args) {
        ResourceManager resourceManager = ResourceManagerFactory.create().registryResourceQueue(
                new ResourceQueue(tag, new RamQueueStorePlanner(), ResourceSetting.create().setLock(true).setLockForceLeaseDuration(100), new ResourceLoader() {
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
        for (int i = 0; i < 1000000; i++) {
            ResourceItem resourceItem = resourceManager.allocate(tag);
            String data = "null";
            if (resourceItem != null) {
                feedBackRandom(tag, resourceManager, resourceItem);
                //feedBackAlways(tag, resourceManager, resourceItem);
                data = resourceItem.getData();
            }
            if (i % 100 == 0) {
                printQueueStatus(resourceManager, tag);
            }

        }

        printQueueStatus(resourceManager, tag);

    }

    private static void feedBackAlways(String tag, ResourceManager resourceManager, ResourceItem resourceItem) {
        int i = ThreadLocalRandom.current().nextInt(10);
        resourceManager.feedBack(tag, resourceItem.getKey(), i > 8);
    }

    private static void feedBackRandom(String tag, ResourceManager resourceManager, ResourceItem resourceItem) {
        int i = ThreadLocalRandom.current().nextInt(10);
        if (i > 8) {
            return;
        }
        feedBackAlways(tag, resourceManager, resourceItem);
    }

    private static void printQueueStatus(ResourceManager resourceManager, String tag) {
        AllResourceItems allResourceItems = resourceManager.queueStatus(tag);
        System.out.println(allResourceItems.getPollingQueue().size() + "  " + allResourceItems.getLeaveQueue().size() + "   " + allResourceItems.getForbiddenQueue().size());
    }
}
