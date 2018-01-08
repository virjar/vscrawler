package com.virjar.vscrawler.resourcemanager.test;

import com.virjar.vscrawler.resourcemanager.ResourceManager;
import com.virjar.vscrawler.resourcemanager.ResourceManagerFactory;
import com.virjar.vscrawler.resourcemanager.model.ResourceItem;
import com.virjar.vscrawler.resourcemanager.model.ResourceSetting;
import com.virjar.vscrawler.resourcemanager.service.RamQueueStore;
import com.virjar.vscrawler.resourcemanager.service.ResourceLoader;
import com.virjar.vscrawler.resourcemanager.service.ResourceQueue;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;

/**
 * Created by virjar on 2018/1/8.<br/>基本测试
 */
public class ImeiTest {
    public static void main(String[] args) {
        ResourceQueue resourceQueue = new ResourceQueue("android_imei", new RamQueueStore(), ResourceSetting.create().setLock(true), new ResourceLoader() {
            private BufferedReader reader = new BufferedReader(new InputStreamReader(ImeiTest.class.getResourceAsStream("/imei.txt")));
            private static final int batchSize = 100;
            private boolean closed = false;

            @Override
            public boolean loadResource(Collection<ResourceItem> resourceItems) {
                if (closed) {
                    return false;
                }
                String line;
                int readSize = 0;
                try {
                    while ((line = reader.readLine()) != null) {
                        ResourceItem resourceItem = new ResourceItem();
                        resourceItem.setData(line);
                        resourceItem.setKey(line.split(",")[0].trim());
                        resourceItems.add(resourceItem);
                        readSize++;
                        if (readSize > batchSize) {
                            return true;
                        }
                    }
                    IOUtils.closeQuietly(reader);
                    closed = true;
                    return false;
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    IOUtils.closeQuietly(reader);
                    closed = true;
                    return false;
                }

            }
        });
        ResourceManager resourceManager = ResourceManagerFactory.create().registryResourceQueue(resourceQueue).build();
        int allocatedTimes = 0;
        int notAllocatedTimes = 0;
        for (int i = 0; i < 1000; i++) {
            ResourceItem resourceItem = resourceManager.allocate("android_imei");
            if (resourceItem != null) {
                if (i < 50) {
                    resourceManager.feedBack("android_imei", resourceItem.getKey(), true);
                } else if (i < 100) {
                    resourceManager.feedBack("android_imei", resourceItem.getKey(), false);
                } else if (i < 150) {
                    resourceManager.forbidden("android_imei", resourceItem.getKey());
                }
                allocatedTimes++;
                System.out.println(resourceItem.getData());
            } else {
                notAllocatedTimes++;
                System.out.println("none");
            }
        }
        System.out.println("allocatedTimes: " + allocatedTimes + "  notAllocatedTimes: " + notAllocatedTimes);
    }


}
