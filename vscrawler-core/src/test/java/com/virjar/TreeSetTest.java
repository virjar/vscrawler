package com.virjar;

import com.google.common.collect.Sets;
import com.virjar.vscrawler.core.resourcemanager.model.ResourceItem;

import java.util.TreeSet;

/**
 * Created by virjar on 2018/7/14.
 */
public class TreeSetTest {

    private static class ResourceItemHolder implements Comparable<ResourceItemHolder> {
        private ResourceItem resourceItem;

        public ResourceItemHolder(ResourceItem resourceItem) {
            this.resourceItem = resourceItem;
        }

        @Override
        public int compareTo(ResourceItemHolder o) {
            return Long.valueOf(resourceItem.getValidTimeStamp()).compareTo(o.resourceItem.getValidTimeStamp());
        }
    }

    public static void main(String[] args) {
        TreeSet<ResourceItemHolder> treeSet = Sets.newTreeSet();
        ResourceItem resourceItem = new ResourceItem();
        resourceItem.setKey("123");
        resourceItem.setData(resourceItem.getKey());
        resourceItem.setValidTimeStamp(100);
        treeSet.add(new ResourceItemHolder(resourceItem));

        resourceItem = new ResourceItem();
        resourceItem.setKey("12");
        resourceItem.setData(resourceItem.getKey());
        resourceItem.setValidTimeStamp(100);
        treeSet.add(new ResourceItemHolder(resourceItem));

        for (ResourceItemHolder resourceItemHolder : treeSet) {
            System.out.println(resourceItemHolder.resourceItem.getKey());
        }
    }
}
