package com.virjar.vscrawler.core.seed;

/**
 * Created by virjar on 17/5/16.
 */
public class DefaultSeedKeyResolver implements SeedKeyResolver {
    @Override
    public String resolveSeedKey(Seed seed) {
        return seed.getData();
    }
}
