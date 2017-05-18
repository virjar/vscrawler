package com.virjar.vscrawler.seed;

import java.util.Collection;
import java.util.Properties;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import com.virjar.vscrawler.util.SingtonObjectHolder;
import com.virjar.vscrawler.util.VSCrawlerConstant;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by virjar on 17/5/16.
 */
@Slf4j
public class LocalFileSeedSource implements InitSeedSource {
    @Override
    public Collection<Seed> initSeeds() {
        Properties properties = SingtonObjectHolder.vsCrawlerConfigFileWatcher.loadedProperties();
        String seedFilePath = properties.getProperty(VSCrawlerConstant.VSCRAWLER_INIT_SEED_FILE);
        if (StringUtils.isEmpty(seedFilePath)) {
            log.info("没有配置初始");
        }
        return Lists.newArrayList();
    }
}
