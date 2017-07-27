package com.virjar.vscrawler.core.seed;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by virjar on 17/7/27.
 */
public class HtmlPageSeedKeyResolver implements SeedKeyResolver {
    private static final Pattern fileNamePattern = Pattern.compile("([^#]*).*");

    @Override
    public String resolveSeedKey(Seed seed) {
        Matcher matcher = fileNamePattern.matcher(seed.getData());
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return "#";
    }
}
