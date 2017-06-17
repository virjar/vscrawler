package com.virjar.vscrawler.core.processor;

import org.apache.commons.lang3.StringUtils;

import com.virjar.vscrawler.core.seed.Seed;

/**
 * Created by virjar on 17/6/17.
 */
public abstract class PreffixSeedRouter implements SeedRouter {
    private String prefix;

    public PreffixSeedRouter(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean matchSeed(Seed seed) {
        return StringUtils.startsWithIgnoreCase(seed.getData(), prefix);
    }
}
