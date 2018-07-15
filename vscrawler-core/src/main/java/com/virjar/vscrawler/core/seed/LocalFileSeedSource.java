package com.virjar.vscrawler.core.seed;

import com.google.common.collect.Lists;
import com.google.common.io.LineReader;
import com.virjar.vscrawler.core.VSCrawlerContext;
import com.virjar.vscrawler.core.event.systemevent.NewSeedArrivalEvent;
import com.virjar.vscrawler.core.util.PathResolver;
import com.virjar.vscrawler.core.util.VSCrawlerConstant;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileReader;
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
public class LocalFileSeedSource implements InitSeedSource, LoadNextBatchSeedEvent {
    @NonNull
    private String filePath;

    /**
     * 由于用户的实际文件可能非常大,如果一瞬间全部读入到任务库,会导致内存压力,也会引起卡顿,所以分批写入到任务库
     */
    private static final int batchSize = 2048;
    private LineReader lineReader;
    private FileReader fileReader;

    @Override
    public void nextBatch(VSCrawlerContext vsCrawlerContext) {
        //这里输入将会交给事件循环来投递种子
        Collection<Seed> seeds = null;
        try {
            seeds = readBatch();
            if (seeds.size() > 0) {
                vsCrawlerContext.getAutoEventRegistry().findEventDeclaring(NewSeedArrivalEvent.class).newSeed(vsCrawlerContext, seeds);
            }
        } finally {
            closeOrReadNextBatch(seeds, vsCrawlerContext);
        }

    }


    private Collection<Seed> readBatch() {
        List<Seed> seeds = Lists.newLinkedList();
        try {
            String line;
            int loadSize = 0;
            while ((line = lineReader.readLine()) != null && loadSize < batchSize) {
                if (StringUtils.isBlank(line)) {
                    continue;
                }
                loadSize++;
                seeds.add(new Seed(line));
            }
        } catch (IOException ioe) {
            log.error("error when load init seed resource", ioe);
        }
        return seeds;
    }

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
        vsCrawlerContext.getAutoEventRegistry().registerEvent(LoadNextBatchSeedEvent.class);
        Collection<Seed> seeds = null;
        try {
            fileReader = new FileReader(new File(PathResolver.resolveAbsolutePath(seedFilePath)));
            lineReader = new LineReader(fileReader);
            seeds = readBatch();
            return seeds;
        } catch (IOException e) {
            log.error("error when load init seed resource");
            return Collections.emptyList();
        } finally {
            closeOrReadNextBatch(seeds, vsCrawlerContext);
        }
    }

    private void closeOrReadNextBatch(Collection<Seed> seeds, VSCrawlerContext vsCrawlerContext) {
        if (seeds != null && seeds.size() > 0) {
            vsCrawlerContext.getAutoEventRegistry().findEventDeclaring(LoadNextBatchSeedEvent.class).nextBatch(vsCrawlerContext);
            return;
        }
        IOUtils.closeQuietly(fileReader);
        fileReader = null;
        lineReader = null;

    }
}


//interface LoadNextBatchSeedEvent {
//    @AutoEvent
//    void nextBatch(VSCrawlerContext vsCrawlerContext);
//}