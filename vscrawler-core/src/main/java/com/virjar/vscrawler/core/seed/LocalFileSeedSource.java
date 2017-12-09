package com.virjar.vscrawler.core.seed;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.virjar.vscrawler.core.VSCrawlerContext;
import com.virjar.vscrawler.core.util.PathResolver;
import com.virjar.vscrawler.core.util.VSCrawlerConstant;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Created by virjar on 17/5/16.<br/>
 * 从本地文件加载种子,一行一条数据
 */
@Slf4j
@NoArgsConstructor
@RequiredArgsConstructor
public class LocalFileSeedSource implements InitSeedSource {
    @NonNull
    private String filePath;

    @Override
    public Collection<Seed> initSeeds(VSCrawlerContext vsCrawlerContext) {
        Properties properties = VSCrawlerContext.vsCrawlerConfigFileWatcher.loadedProperties();
        String seedFilePath = PathResolver.resolveAbsolutePath(properties.getProperty(String.format(VSCrawlerConstant.VSCRAWLER_INIT_SEED_FILE, vsCrawlerContext.getCrawlerName())));
        if (StringUtils.isBlank(seedFilePath) || !new File(seedFilePath).exists()) {
            if (StringUtils.isNotBlank(seedFilePath)) {
                log.warn("can not find file:{}", seedFilePath);
            }
            seedFilePath = PathResolver.resolveAbsolutePath(filePath);
        }
        if (StringUtils.isEmpty(seedFilePath) || !new File(seedFilePath).exists()) {
            if (StringUtils.isNotBlank(seedFilePath)) {
                log.warn("can not find file:{}", seedFilePath);
            }
            return Collections.emptyList();
        }
        try {
            return Files.readLines(new File(PathResolver.resolveAbsolutePath(seedFilePath)),
                    Charsets.UTF_8, new LineProcessor<List<Seed>>() {
                        List<Seed> seeds = Lists.newLinkedList();

                        @Override
                        public boolean processLine(String line) throws IOException {
                            if (StringUtils.isBlank(line)) {
                                return true;
                            }
                            seeds.add(new Seed(line));
                            return true;
                        }

                        @Override
                        public List<Seed> getResult() {
                            return seeds;
                        }
                    });
        } catch (IOException e) {
            log.error("error when load init seed resource");
            return Collections.emptyList();
        }

    }
}
