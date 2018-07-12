package com.virjar.vscrawler.core.seed;

import com.google.common.collect.*;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import com.sleepycat.je.*;
import com.virjar.vscrawler.core.VSCrawlerContext;
import com.virjar.vscrawler.core.event.systemevent.CrawlerConfigChangeEvent;
import com.virjar.vscrawler.core.event.systemevent.CrawlerEndEvent;
import com.virjar.vscrawler.core.event.systemevent.FirstSeedPushEvent;
import com.virjar.vscrawler.core.event.systemevent.NewSeedArrivalEvent;
import com.virjar.vscrawler.core.util.VSCrawlerCommonUtil;
import com.virjar.vscrawler.core.util.VSCrawlerConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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

    private SegmentResolver segmentResolver;

    private DatabaseConfig databaseConfig;


    private AtomicBoolean isSeedEmpty = new AtomicBoolean(false);

    // 内部cache,数据加载到内存,以对象的方式存储,避免db操作带来锁的压力
    private ConcurrentLinkedQueue<Seed> ramCache = new ConcurrentLinkedQueue<>();
    // 所有正在处理的种子
    private Map<String, Seed> runningSeeds = Maps.newConcurrentMap();
    private volatile boolean isClosed = false;
    private ReentrantLock dbLock = new ReentrantLock();
    private Condition dbRelease = dbLock.newCondition();
    private volatile int dbOperator = 0;
    private int cacheSize;
    private VSCrawlerContext vsCrawlerContext;
    private ConcurrentMap<String, Database> openedDataBase = Maps.newConcurrentMap();
    /**
     * 段信息
     */
    private TreeSet<Long> allSegments = Sets.newTreeSet();
    private TreeSet<Long> runningSegments = Sets.newTreeSet();
    ////////// 以下为常量数据
    /**
     * 段表
     */
    private static final String SEGMENT = "SEGMENT_TABLE";

    private static final String RUNNING_SEGMENT_PREFIX = "RUNNING_SEGMENT_PREFIX_";

    private static final String FINISHED_SEGMENT_PREFIX = "FINISHED_SEGMENT_PREFIX_";

    private static final String defaultSegment = "defaultSegment";

    /**
     * 这个方法和pool必须在同一个线程里面
     */
    public void init() {
        // 移植初始种子信息
        migrateInitSeed();
    }

    public BerkeleyDBSeedManager(final VSCrawlerContext vsCrawlerContext, InitSeedSource initSeedSource, SeedKeyResolver seedKeyResolver,
                                 SegmentResolver segmentResolver, int cacheSize) {
        this.vsCrawlerContext = vsCrawlerContext;
        this.initSeedSource = initSeedSource;
        this.seedKeyResolver = seedKeyResolver;
        this.segmentResolver = segmentResolver;
        this.cacheSize = cacheSize;
        // 配置数据库环境
        configEnv();

        // 初始化分段数据库
        loadSegments();

        // 监听消息
        vsCrawlerContext.getAutoEventRegistry().registerObserver(this);

        Runtime.getRuntime().addShutdownHook(new Thread("DBAutoCloseThread") {
            @Override
            public void run() {
                BerkeleyDBSeedManager.this.crawlerEnd(vsCrawlerContext);
            }
        });
    }

    private void loadSegments() {
        Database iteratorDatabases = createOrGetDataBase(SEGMENT);// env.openDatabase(null, SEGMENT, databaseConfig);
        Cursor cursor = iteratorDatabases.openCursor(null, CursorConfig.DEFAULT);
        DatabaseEntry iteratorKey = new DatabaseEntry();
        DatabaseEntry iteratorValue = new DatabaseEntry();

        try {
            while (cursor.getNext(iteratorKey, iteratorValue, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                String segmentName = new String(iteratorValue.getData());
                allSegments.add(Long.parseLong(segmentName));
                runningSegments.add(Long.parseLong(segmentName));
            }
        } finally {
            IOUtils.closeQuietly(cursor);
            // IOUtils.closeQuietly(iteratorDatabases);
        }
    }

    private synchronized void loadCache() {
        // step one ,attempt load default segment
        loadCache(defaultSegment);
        if (ramCache.size() >= cacheSize) {
            return;
        }

        // step two ,attempt to load time segment
        Iterator<Long> iterator = runningSegments.iterator();
        while (iterator.hasNext()) {
            Long activeTimeStamp = iterator.next();

            // segment need to be active in the future
            if (activeTimeStamp > System.currentTimeMillis()) {
                return;
            }
            if (loadCache(String.valueOf(activeTimeStamp)) == 0) {
                iterator.remove();
            }
            if (ramCache.size() >= cacheSize) {
                return;
            }
        }
    }

    private Database createOrGetDataBase(String key) {
        if (openedDataBase.containsKey(key)) {
            return openedDataBase.get(key);
        }
        synchronized (this) {
            if (openedDataBase.containsKey(key)) {
                return openedDataBase.get(key);
            }
            Database database = env.openDatabase(null, key, databaseConfig);
            openedDataBase.put(key, database);
            return database;
        }
    }

    private void closeAllDatabase() {
        for (String databaseName : openedDataBase.keySet()) {
            IOUtils.closeQuietly(openedDataBase.remove(databaseName));
        }
    }

    private synchronized int loadCache(String segmentName) {
        int loadSize = 0;
        Database iteratorDatabases = null;
        Cursor cursor = null;
        Database finishedSeedDatabase = null;
        if (isClosed) {
            return 0;
        }
        DatabaseEntry iteratorKey = new DatabaseEntry();
        DatabaseEntry iteratorValue = new DatabaseEntry();

        try {
            lockDBOperate();

            iteratorDatabases = createOrGetDataBase(RUNNING_SEGMENT_PREFIX + segmentName); //env.openDatabase(null, RUNNING_SEGMENT_PREFIX + segmentName, databaseConfig);
            cursor = iteratorDatabases.openCursor(null, CursorConfig.DEFAULT);
            finishedSeedDatabase = createOrGetDataBase(FINISHED_SEGMENT_PREFIX + segmentName); //env.openDatabase(null, FINISHED_SEGMENT_PREFIX + segmentName, databaseConfig);
            while (cursor.getNext(iteratorKey, iteratorValue, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                loadSize++;
                Seed ret = VSCrawlerCommonUtil.transferStringToSeed(new String(iteratorValue.getData()));
                ret.setSegmentKey(segmentName);
                cursor.delete();// 删除当前数据
                if (!ret.needEnd()) {
                    ramCache.offer(ret);
                    if (ramCache.size() >= cacheSize) {
                        break;
                    }
                } else {
                    finishedSeedDatabase.put(null, iteratorKey, iteratorValue);
                }
            }
        } finally {
            unlockDBOperate();
            IOUtils.closeQuietly(cursor);
        }
        return loadSize;
    }

    public synchronized Seed poll() {
        if (isClosed) {
            return null;
        }
        if (ramCache.size() == 0) {
            loadCache();
        }
        if (ramCache.size() == 0) {
            this.isSeedEmpty.set(true);
            return null;
        } else {
            Seed poll = ramCache.poll();
            if (poll != null) {
                runningSeeds.put(poll.getSegmentKey() + seedKeyResolver.resolveSeedKey(poll), poll);
            }
            return poll;
        }
    }

    private void configEnv() {
        resolveDBFile();
        EnvironmentConfig environmentConfig = new EnvironmentConfig();
        environmentConfig.setAllowCreate(true);
        try {
            env = new Environment(new File(dbFilePath), environmentConfig);
        } catch (EnvironmentLockedException e) {
            if (new File(dbFilePath, "je.lck").delete()) {
                log.warn("上次未正常关闭爬虫,尝试修复");
                env = new Environment(new File(dbFilePath), environmentConfig);
            } else {
                log.error("存在多个爬虫操作同一份数据,请确认多个爬虫的工作空间是否相同", e);
                throw e;
            }

        }

        databaseConfig = new DatabaseConfig();
        databaseConfig.setAllowCreate(true);
    }

    /**
     * 如果用户配置了初始的种子源那么启动时加载种子源
     */
    private void migrateInitSeed() {
        Collection<Seed> seeds = initSeedSource.initSeeds(vsCrawlerContext);
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
    public void finish(Seed seed) {
        if (isClosed) {
            log.info("db已经关闭,拒绝归还任务");
            return;
        }
        String seedKey = seedKeyResolver.resolveSeedKey(seed);
        runningSeeds.remove(seed.getSegmentKey() + seedKey);

        DatabaseEntry key = new DatabaseEntry(seedKey.getBytes());
        DatabaseEntry value = new DatabaseEntry(VSCrawlerCommonUtil.transferSeedToString(seed).getBytes());
        try {
            if (isClosed) {
                log.info("db已经关闭,拒绝归还任务");
                return;
            }
            lockDBOperate();

            if (seed.needEnd()) {
                Database finishedSeedDatabase = createOrGetDataBase(FINISHED_SEGMENT_PREFIX + seed.getSegmentKey());
                finishedSeedDatabase.put(null, key, value);

                Database runningSeedDatabase = createOrGetDataBase(RUNNING_SEGMENT_PREFIX + seed.getSegmentKey());
                runningSeedDatabase.removeSequence(null, key);
            } else {
                Database runningSeedDatabase = createOrGetDataBase(RUNNING_SEGMENT_PREFIX + seed.getSegmentKey());
                runningSeedDatabase.put(null, key, value);
            }
        } finally {
            unlockDBOperate();
        }

    }

    private void reSaveCache() {
        LinkedList<Seed> allSeed = Lists.newLinkedList();
        allSeed.addAll(ramCache);
        allSeed.addAll(runningSeeds.values());
        // 转化为各自的段
        Multimap<String, Seed> segmentSeeds = HashMultimap.create();
        for (Seed seed : allSeed) {
            segmentSeeds.put(seed.getSegmentKey(), seed);
        }

        // 处理各自的段
        for (Map.Entry<String, Collection<Seed>> entry : segmentSeeds.asMap().entrySet()) {
            Database runningSeedDatabase = null;
            try {
                lockDBOperate();
                runningSeedDatabase = env.openDatabase(null, RUNNING_SEGMENT_PREFIX + entry.getKey(), databaseConfig);
                for (Seed seed : entry.getValue()) {
                    DatabaseEntry key = new DatabaseEntry(seedKeyResolver.resolveSeedKey(seed).getBytes());
                    DatabaseEntry value = new DatabaseEntry(VSCrawlerCommonUtil.transferSeedToString(seed).getBytes());
                    runningSeedDatabase.put(null, key, value);
                }

            } finally {
                unlockDBOperate();
                IOUtils.closeQuietly(runningSeedDatabase);
            }

        }
    }

    private void saveSegment() {
        Database iteratorDatabases = null;
        try {
            lockDBOperate();
            iteratorDatabases = createOrGetDataBase(SEGMENT);// env.openDatabase(null, SEGMENT, databaseConfig);
            for (Long segment : allSegments) {
                DatabaseEntry key = new DatabaseEntry(segment.toString().getBytes());
                DatabaseEntry value = new DatabaseEntry(segment.toString().getBytes());
                iteratorDatabases.put(null, key, value);
            }
        } finally {
            unlockDBOperate();
            // IOUtils.closeQuietly(iteratorDatabases);
        }
    }

    private void lockDBOperate() {
        dbLock.lock();
        dbOperator++;
        dbLock.unlock();
    }

    private void unlockDBOperate() {
        dbLock.lock();
        dbOperator--;
        dbRelease.signal();
        dbLock.unlock();
    }

    /**
     * 新产生的种子,如果入库,那么会消重。后加入的种子被reject
     *
     * @param seeds 种子
     */
    public void addNewSeeds(Collection<Seed> seeds) {
        if (isClosed) {
            log.warn("db已经关闭,拒绝添加新种子");
            return;
        }

        // 转化为各自的段
        Multimap<String, Seed> segmentSeeds = HashMultimap.create();
        for (Seed seed : seeds) {
            if (seed.getActiveTimeStamp() != null) {
                segmentSeeds.put(String.valueOf(segmentResolver.resolveSegmentKey(seed.getActiveTimeStamp())), seed);
            } else {
                segmentSeeds.put(defaultSegment, seed);
            }
        }

        int realAddSeedNumber = 0;
        // 处理各自的段
        for (Map.Entry<String, Collection<Seed>> entry : segmentSeeds.asMap().entrySet()) {
            try {
                lockDBOperate();
                Database runningSeedDatabase = createOrGetDataBase(RUNNING_SEGMENT_PREFIX + entry.getKey());// env.openDatabase(null, RUNNING_SEGMENT_PREFIX + entry.getKey(), databaseConfig);
                Database finishedSeedDatabase = createOrGetDataBase(FINISHED_SEGMENT_PREFIX + entry.getKey());// env.openDatabase(null, FINISHED_SEGMENT_PREFIX + entry.getKey(), databaseConfig);
                for (Seed seed : entry.getValue()) {
                    /**
                     * 处理新增加的段
                     */
                    if (!StringUtils.equals(entry.getKey(), defaultSegment)
                            && !allSegments.contains(Long.parseLong(entry.getKey()))) {
                        allSegments.add(Long.parseLong(entry.getKey()));
                        runningSegments.add(Long.parseLong(entry.getKey()));
                    }

                    DatabaseEntry key = new DatabaseEntry(seedKeyResolver.resolveSeedKey(seed).getBytes());
                    DatabaseEntry valueEntry = new DatabaseEntry();
                    // db层面消重
                    if (runningSeedDatabase.get(null, key, valueEntry, LockMode.DEFAULT) == OperationStatus.SUCCESS ||
                            finishedSeedDatabase.get(null, key, valueEntry, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                        continue;
                    }

                    DatabaseEntry value = new DatabaseEntry(VSCrawlerCommonUtil.transferSeedToString(seed).getBytes());
                    runningSeedDatabase.putNoOverwrite(null, key, value);
                    realAddSeedNumber++;
                    if (isSeedEmpty.compareAndSet(true, false)) {
                        if (seed.getActiveTimeStamp() == null) {
                            vsCrawlerContext.getAutoEventRegistry().findEventDeclaring(FirstSeedPushEvent.class)
                                    .firstSeed(vsCrawlerContext, seed);
                        } else {
                            // 如果这种子是在未来执行,那么发送未来的延时消息
                            vsCrawlerContext.getAutoEventRegistry().createDelayEventSender(FirstSeedPushEvent.class)
                                    .sendDelay(seed.getActiveTimeStamp() - System.currentTimeMillis() + 10L).delegate()
                                    .firstSeed(vsCrawlerContext, seed);
                        }
                    }
                }
                log.info("实际导入种子数量:{}", realAddSeedNumber);
            } finally {
                unlockDBOperate();
            }
        }

    }

    private void resolveDBFile() {
        // 配置数据库文件地址
        File dbFile = new File(vsCrawlerContext.getWorkPath(), "berkeleyDB");
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
    public void configChange(VSCrawlerContext vsCrawlerContext, Properties newProperties) {

    }

    @Override
    public void newSeed(VSCrawlerContext vsCrawlerContext, Collection<Seed> newSeeds) {
        addNewSeeds(newSeeds);
    }

    @Override
    public void crawlerEnd(VSCrawlerContext vsCrawlerContext) {
        if (isClosed) {
            return;
        }
        isClosed = true;
        log.info("收到爬虫结束消息,开始关闭资源");
        log.info("拒绝抓取结果入库...");
        log.info("缓存中未分发数据重新入库,正在执行的爬虫任务,不等待结果,重新入库...");
        reSaveCache();
        log.info("写入段表信息");
        saveSegment();
        log.info("关闭数据库环境...");
        while (true) {
            dbLock.lock();
            if (dbOperator <= 0) {
                closeAllDatabase();
                IOUtils.closeQuietly(env);
                dbLock.unlock();
                break;
            }
            try {
                dbRelease.await();
            } catch (InterruptedException e) {
                closeAllDatabase();
                IOUtils.closeQuietly(env);
                throw new IllegalStateException("can not close db ,db operate await Interrupted");
            }
            dbLock.unlock();
        }
    }

    public void clear() {
        List<String> databaseNames = env.getDatabaseNames();
        long expectedNumber = NumberUtils.toLong(VSCrawlerContext.vsCrawlerConfigFileWatcher.loadedProperties()
                .getProperty(VSCrawlerConstant.VSCRAWLER_SEED_MANAGER_EXPECTED_SEED_NUMBER), 1000000L);
        synchronized (this) {
            closeAllDatabase();
            for (Long segment : allSegments) {
                String segmentName = RUNNING_SEGMENT_PREFIX + segment;
                if (databaseNames.contains(segmentName)) {
                    env.removeDatabase(null, segmentName);
                }

                segmentName = FINISHED_SEGMENT_PREFIX + segment;
                if (databaseNames.contains(segmentName)) {
                    env.removeDatabase(null, segmentName);
                }
            }

            // default segment
            String segmentName = RUNNING_SEGMENT_PREFIX + defaultSegment;
            if (databaseNames.contains(segmentName)) {
                env.removeDatabase(null, segmentName);
            }

            segmentName = FINISHED_SEGMENT_PREFIX + defaultSegment;
            if (databaseNames.contains(segmentName)) {
                env.removeDatabase(null, segmentName);
            }

        }
    }

    public long finishedSeed() {
        if (isClosed) {
            return 0;
        }
        long recordSize = 0;
        // default segment
        String segmentName = FINISHED_SEGMENT_PREFIX + defaultSegment;
        recordSize += createOrGetDataBase(segmentName).count();
        for (Long segment : allSegments) {
            segmentName = FINISHED_SEGMENT_PREFIX + segment;
            recordSize += createOrGetDataBase(segmentName).count();
        }
        return recordSize;
    }

    public long totalSeed() {
        if (isClosed) {
            return 0;
        }
        long recordSize = 0;
        for (Long segment : allSegments) {
            String segmentName = RUNNING_SEGMENT_PREFIX + segment;
            recordSize += createOrGetDataBase(segmentName).count();
        }
        // default segment
        String segmentName = RUNNING_SEGMENT_PREFIX + defaultSegment;
        recordSize += createOrGetDataBase(segmentName).count();
        return recordSize + finishedSeed();
    }
}
