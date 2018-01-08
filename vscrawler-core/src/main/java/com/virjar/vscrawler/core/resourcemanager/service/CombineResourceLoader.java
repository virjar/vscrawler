package com.virjar.vscrawler.core.resourcemanager.service;

import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.resourcemanager.model.ResourceItem;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by virjar on 2018/1/8.<br/>
 *
 * @author virjar
 * @since 0.2.2
 * 有多个初始化数据源,将会在上游merge
 */
public class CombineResourceLoader implements ResourceLoader {
    private LinkedList<ResourceLoader> delegates;

    public CombineResourceLoader(Collection<ResourceLoader> delegates) {
        this.delegates = Lists.newLinkedList(delegates);
    }

    public void addNewResourceLoader(ResourceLoader resourceLoader){
        delegates.add(resourceLoader);
    }

    @Override
    public synchronized boolean loadResource(Collection<ResourceItem> resourceItems) {
        if (delegates.size() == 0) {
            return false;
        }
        boolean hasNextBatch = delegates.get(0).loadResource(resourceItems);
        if (!hasNextBatch) {
            delegates.removeFirst();
        }
        return delegates.size() > 0;
    }
}
