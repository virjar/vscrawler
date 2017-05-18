package com.virjar.vscrawler.seed;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import com.sleepycat.je.*;
import com.virjar.vscrawler.event.support.AutoEventRegistry;
import com.virjar.vscrawler.event.systemevent.CrawlerConfigChangeEvent;
import com.virjar.vscrawler.event.systemevent.CrawlerEndEvent;
import com.virjar.vscrawler.event.systemevent.NewSeedArrivalEvent;
import com.virjar.vscrawler.util.PathResolver;
import com.virjar.vscrawler.util.SingtonObjectHolder;
import com.virjar.vscrawler.util.VSCrawlerCommonUtil;
import com.virjar.vscrawler.util.VSCrawlerConstant;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by virjar on 17/5/14. <br/>
 * 伯克利数据库,用来管理种子任务
 *
 * @author virjar
 * @since 0.0.1
 */
@Slf4j
public class BerkeleyDBSeedManager implements CrawlerConfigChangeEvent, NewSeedArrivalEvent, CrawlerEndEvent {

    private Environment env;

    private String dbFilePath;

    private InitSeedSource initSeedSource;

    private SeedKeyResolver seedKeyResolver;

    private BloomFilter<Seed> bloomFilter;

    private DatabaseConfig databaseConfig;

    private Cursor cursor;

    private Database iteratorDatabases;

    protected DatabaseEntry iteratorKey = new DatabaseEntry();
    protected DatabaseEntry iteratorValue = new DatabaseEntry();

    public BerkeleyDBSeedManager(InitSeedSource initSeedSource, SeedKeyResolver seedKeyResolver) {
        this.initSeedSource = initSeedSource;
        this.seedKeyResolver = seedKeyResolver;
        // 配置数据库环境
        configEnv();

        // 布隆过滤器数据还原
        buildBloomFilterInfo();

        // 移植初始种子信息
        migrateInitSeed();

        // 移植游标
        initCursor();
        // 监听消息
        AutoEventRegistry.getInstance().registerObserver(this);
    }

    public Seed pool() {
        while (true) {
            if (cursor.getNext(iteratorKey, iteratorValue, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                try {
                    return VSCrawlerCommonUtil.transferStringToSeed(new String(iteratorValue.getData()));
                } catch (Exception ex) {
                    log.warn("Exception when generating", ex);
                }
            } else {
                return null;
            }
        }
    }

    private void initCursor() {
        iteratorDatabases = env.openDatabase(null, "crawlSeed", databaseConfig);
        cursor = iteratorDatabases.openCursor(null, CursorConfig.DEFAULT);
    }

    private boolean saveBloomFilterInfo() {
        File bloomData = new File(SingtonObjectHolder.workPath, "bloomFilter.dat");
        if (bloomData.exists()) {
            try {
                if (!bloomData.createNewFile()) {
                    return false;
                }
            } catch (IOException ioe) {
                log.error("cannot serialize bloomFilter data", ioe);
                return false;
            }
        }

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(bloomData);
            bloomFilter.writeTo(fileOutputStream);
            return true;
        } catch (IOException ioe) {
            log.warn("不能写入取BloomFilter数据,消重逻辑可能转移到数据库,性能可能受到影响", ioe);
            return false;
        } finally {
            IOUtils.closeQuietly(fileOutputStream);
        }
    }

    private void buildBloomFilterInfo() {
        File bloomData = new File(SingtonObjectHolder.workPath, "bloomFilter.dat");
        if (bloomData.exists()) {
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(bloomData);
                bloomFilter = BloomFilter.readFrom(inputStream, new Funnel<Seed>() {
                    @Override
                    public void funnel(Seed from, PrimitiveSink into) {
                        into.putString(seedKeyResolver.resolveSeedKey(from), Charset.defaultCharset());
                    }
                });
            } catch (IOException ioe) {
                log.warn("不能读取BloomFilter数据,消重逻辑可能转移到数据库,性能可能受到影响", ioe);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        }

        long expectedNumber = NumberUtils.toLong(SingtonObjectHolder.vsCrawlerConfigFileWatcher.loadedProperties()
                .getProperty(VSCrawlerConstant.VSCRAWLER_SEED_MANAGER_EXPECTED_SEED_NUMBER), 1000000L);

        // any way, build a filter instance if not exist
        if (bloomFilter == null) {
            bloomFilter = BloomFilter.create(new Funnel<Seed>() {
                @Override
                public void funnel(Seed from, PrimitiveSink into) {
                    into.putString(seedKeyResolver.resolveSeedKey(from), Charset.defaultCharset());
                }
            }, expectedNumber);
        }
        // can not migrate if expectedNumber not equals, else check will failed
        /*
         * else { BloomFilter<Seed> temp = BloomFilter.create(new Funnel<Seed>() {
         * @Override public void funnel(Seed from, PrimitiveSink into) {
         * into.putString(seedKeyResolver.resolveSeedKey(from), Charset.defaultCharset()); } }, expectedNumber);
         * temp.putAll(bloomFilter); bloomFilter = temp; }
         */
    }

    private void configEnv() {
        resolveDBFile();
        EnvironmentConfig environmentConfig = new EnvironmentConfig();
        environmentConfig.setAllowCreate(true);
        env = new Environment(new File(dbFilePath), environmentConfig);

        databaseConfig = new DatabaseConfig();
        databaseConfig.setAllowCreate(true);
    }

    /**
     * 如果用户配置了初始的种子源那么启动时加载种子源
     */
    private void migrateInitSeed() {
        Collection<Seed> seeds = initSeedSource.initSeeds();
        if (seeds == null) {
            return;
        }
        log.info("import new init seeds:{}", seeds.size());
        addNewSeeds(seeds);
    }

    /**
     * 更新种子,如果种子已经处理完成,那么移动到完成库,否则修改状态
     * 
     * @param seed 曾经处理过的种子
     */
    public void updateSeed(Seed seed) {
        DatabaseEntry key = new DatabaseEntry(seedKeyResolver.resolveSeedKey(seed).getBytes());
        DatabaseEntry value = new DatabaseEntry(VSCrawlerCommonUtil.transferSeedToString(seed).getBytes());

        if (seed.needEnd()) {
            Database finishedSeedDatabase = env.openDatabase(null, "finishedSeed", databaseConfig);
            finishedSeedDatabase.put(null, key, value);
            finishedSeedDatabase.close();
            Database runningSeedDatabase = env.openDatabase(null, "crawlSeed", databaseConfig);
            runningSeedDatabase.removeSequence(null, key);
            runningSeedDatabase.close();
        } else {
            Database runningSeedDatabase = env.openDatabase(null, "crawlSeed", databaseConfig);
            runningSeedDatabase.put(null, key, value);
            runningSeedDatabase.close();
        }
    }

    /**
     * 新产生的种子,如果入库,那么会消重。后加入的种子被reject
     * 
     * @param seeds 种子
     */
    public void addNewSeeds(Collection<Seed> seeds) {
        Database runningSeedDatabase = env.openDatabase(null, "crawlSeed", databaseConfig);
        for (Seed seed : seeds) {

            if (bloomFilter.mightContain(seed)) {
                /*
                 * if (seed.needEnd() && runningSeedDatabase.get(null, key, value, LockMode.DEFAULT) ==
                 * OperationStatus.SUCCESS) { runningSeedDatabase.removeSequence(null, key);
                 * finishedSeedDataBases.put(null, key, value); }
                 */
                continue;
            }
            DatabaseEntry key = new DatabaseEntry(seedKeyResolver.resolveSeedKey(seed).getBytes());
            DatabaseEntry value = new DatabaseEntry(VSCrawlerCommonUtil.transferSeedToString(seed).getBytes());
            runningSeedDatabase.put(null, key, value);
            bloomFilter.put(seed);
        }
        runningSeedDatabase.close();
    }

    private void resolveDBFile() {
        // 配置数据库文件地址
        // TODO 移植这段代码
        Properties properties = SingtonObjectHolder.vsCrawlerConfigFileWatcher.loadedProperties();
        String workpath = properties.getProperty(VSCrawlerConstant.VSCRAWLER_WORKING_DIRECTORY, "classpath:work");

        log.info("vsCrawler配置工作目录:{}", workpath);
        workpath = PathResolver.resolveAbsolutePath(workpath);
        SingtonObjectHolder.workPath = workpath;
        log.info("vsCrawler实际工作目录:{}", workpath);
        File workFile = new File(workpath);
        if (workFile.exists() && !workFile.isDirectory()) {
            throw new IllegalStateException(workpath + "不是目录,无法写入数据");
        }

        if (!workFile.exists()) {
            if (!workFile.mkdirs()) {
                throw new IllegalStateException(workpath + "文件夹创建失败");
            }
        }

        File dbFile = new File(workFile, "berkeleyDB");
        if (!dbFile.exists()) {
            if (!dbFile.mkdirs()) {
                throw new IllegalStateException(dbFile.getAbsolutePath() + "文件夹创建失败");
            }
        } else if (!dbFile.isDirectory()) {
            throw new IllegalStateException(dbFile.getAbsolutePath() + "不是一个文件夹");
        }
        dbFilePath = dbFile.getAbsolutePath();
    }

    @Override
    public void configChange(Properties oldProperties, Properties newProperties) {

    }

    @Override
    public void needSeed(Collection<Seed> newSeeds) {
        addNewSeeds(newSeeds);
    }

    @Override
    public void crawlerEnd() {
        // 关闭游标
        cursor.close();
        // 关闭数据库
        iteratorDatabases.close();

        // 1.7的低版本不支持and方法
        // boolean allSuccess = BooleanUtils.and(VSCrawlerCommonUtil.closeQuietly(env), saveBloomFilterInfo());

        // 关闭数据库环境
        VSCrawlerCommonUtil.closeQuietly(env);

        // 存储bloomFilter的数据
        saveBloomFilterInfo();
    }
}
