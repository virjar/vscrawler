package com.virjar.vscrawler.core.processor;

import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.seed.Seed;

import java.util.Collection;
import java.util.List;

/**
 * Created by virjar on 17/4/16.
 *
 * @author virjar
 * @since 0.0.1
 */
public class CrawlResult {
    /**
     * 一个种子可能产生多个结果
     */
    private List<String> results = Lists.newLinkedList();
    private List<Seed> newSeeds = Lists.newLinkedList();


    public void addResult(String result) {
        results.add(result);
    }

    public void addResults(Collection<String> resultsIn) {
        results.addAll(resultsIn);
    }

    @Deprecated
    public List<String> allResult() {
        return Lists.newArrayList(results);
    }

    public void addSeed(Seed seed) {
        newSeeds.add(seed);
    }

    public void addStrSeeds(Collection<String> seeds) {
        for (String str : seeds) {
            addSeed(str);
        }
    }

    public void addSeeds(Collection<Seed> seeds) {
        for (Seed seed : seeds) {
            addSeed(seed);
        }
    }

    public void addSeed(String seed) {
        newSeeds.add(new Seed(seed));
    }

    public List<Seed> allSeed() {
        return Lists.newArrayList(newSeeds);
    }
}
