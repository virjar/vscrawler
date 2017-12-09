package com.virjar.vscrawler.core.seed;

import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.VSCrawlerContext;
import com.virjar.vscrawler.core.util.VSCrawlerConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Properties;

/**
 * Created by virjar on 17/5/16.
 */
@Slf4j
//TODO 功能还未实现
public class LocalFileSeedSource implements InitSeedSource {
    @Override
    public Collection<Seed> initSeeds() {
        Properties properties = VSCrawlerContext.vsCrawlerConfigFileWatcher.loadedProperties();
        String seedFilePath = properties.getProperty(VSCrawlerConstant.VSCRAWLER_INIT_SEED_FILE);
        if (StringUtils.isEmpty(seedFilePath)) {
            log.info("没有配置初始种子");
        }
        return Lists.newArrayList();
    }
}
