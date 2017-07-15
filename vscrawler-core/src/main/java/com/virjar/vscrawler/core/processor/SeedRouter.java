package com.virjar.vscrawler.core.processor;

import com.virjar.vscrawler.core.seed.Seed;

/**
 * Created by virjar on 17/6/17.
 */
public interface SeedRouter {
    boolean matchSeed(Seed seed);
}
