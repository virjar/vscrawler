package com.virjar.vscrawler.seed;

import java.io.*;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;

/**
 * Created by virjar on 17/4/15.
 * @author virjar
 * @since 0.0.1
 */
public class SeedManager {
    private static final Logger logger = LoggerFactory.getLogger(SeedManager.class);
    private AtomicLong fileCursor;
    private BloomFilter<String> processedSeeds = null;

    // private String seedSignPath;

    private String seedFilePath;

    private String seedConfigPath;

    private ConcurrentLinkedQueue<String> allSeeds = new ConcurrentLinkedQueue<>();

    private LinkedList<String> newSeeds = Lists.newLinkedList();

    private PrintWriter fileUrlWriter;

    private ScheduledExecutorService flushThreadPool;

    public SeedManager(String seedConfigPath) {

        this.seedConfigPath = seedConfigPath;
        Properties seedConfig = new Properties();

        InputStream fileInputStream = null;
        try {
            if (new File(seedConfigPath).exists()) {// 尝试加载绝对路径
                fileInputStream = new FileInputStream(new File(seedConfigPath));
            } else if (SeedManager.class.getResource("/" + seedConfigPath) != null) {
                this.seedConfigPath = SeedManager.class.getResource("/" + seedConfigPath).getFile();
                fileInputStream = SeedManager.class.getResourceAsStream("/" + seedConfigPath);
            } else {
                String file = SeedManager.class.getResource("/").getFile();
                File conFile = new File(file, seedConfigPath);
                this.seedConfigPath = conFile.getAbsolutePath();
                fileInputStream = new FileInputStream(conFile);
            }
            seedConfig.load(fileInputStream);
        } catch (Exception e) {
            logger.error("种子规则加载失败", e);
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }

        // 基本的参数信息
        fileCursor = new AtomicLong(NumberUtils.toLong(seedConfig.getProperty("fileCursor")));
        // seedSignPath = seedConfig.getProperty("seedSignPath", "seedSign.dat");
        seedFilePath = seedConfig.getProperty("seedFilePath", "seed.txt");
        // 一亿大小的空间
        processedSeeds = BloomFilter.create(new StringFunnel(), 100000000);

        // 种子偏移
        readSeedFile();

        try {
            fileUrlWriter = new PrintWriter(new FileWriter(seedFilePath, true));
        } catch (Exception e) {
            logger.error("", e);
        }
        initFlushThread();
    }

    private void initFlushThread() {
        flushThreadPool = Executors.newScheduledThreadPool(1);
        flushThreadPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                dump();
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    public synchronized void dump() {
        // dump种子
        String seed;
        while ((seed = newSeeds.poll()) != null) {
            fileUrlWriter.println(seed);
        }
        fileUrlWriter.flush();

        Properties seedConfig = new Properties();
        seedConfig.setProperty("fileCursor", String.valueOf(fileCursor.get()));
        seedConfig.setProperty("seedFilePath", seedFilePath);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(seedConfigPath));
            seedConfig.store(fileOutputStream, "vscrawler description file");
        } catch (Exception e) {
            logger.error("种子规则保存失败");
        } finally {
            IOUtils.closeQuietly(fileOutputStream);
        }

    }

    public String consumeSeed() {
        String poll = allSeeds.poll();
        if (poll != null) {
            processedSeeds.put(poll);
            fileCursor.incrementAndGet();
        }
        return poll;
    }

    public void addSeed(String newSeed) {
        if (processedSeeds.mightContain(newSeed)) {
            return;
        }
        newSeeds.offer(newSeed);
        allSeeds.offer(newSeed);
    }

    public void addSeedFoce(String newSeed) {
        newSeeds.offer(newSeed);
        allSeeds.offer(newSeed);
    }

    private void readSeedFile() {
        if (!new File(seedFilePath).exists()) {
            return;
        }
        String line;
        BufferedReader fileUrlReader = null;
        try {
            fileUrlReader = new BufferedReader(new FileReader(seedFilePath));
            int lineReaded = 0;
            while ((line = fileUrlReader.readLine()) != null) {
                processedSeeds.put(line);
                lineReaded++;
                if (lineReaded >= fileCursor.get()) {
                    // fileCursor.incrementAndGet();
                    allSeeds.offer(line);
                }
            }
        } catch (Exception e) {
            logger.error("种子文件读取失败", e);
        } finally {
            if (fileUrlReader != null) {
                IOUtils.closeQuietly(fileUrlReader);
            }
        }
    }

    private static class StringFunnel implements Funnel<String> {
        @Override
        public void funnel(String from, PrimitiveSink into) {
            into.putString(from, Charsets.UTF_8);
        }
    }

}
