package com.virjar.vscrawler.core.processor;

import java.util.regex.Pattern;

import com.virjar.vscrawler.core.seed.Seed;

/**
 * Created by virjar on 17/6/17.
 */
public abstract class RegexSeedRouter implements SeedRouter {
    private Pattern pattern;

    public RegexSeedRouter(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public boolean matchSeed(Seed seed) {
        return pattern.matcher(seed.getData()).matches();
    }

}
