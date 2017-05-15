package com.virjar.vscrawler.seed;

import java.io.File;
import java.util.Collection;
import java.util.Properties;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.virjar.vscrawler.event.support.AutoEventRegistry;
import com.virjar.vscrawler.event.systemevent.CrawlerConfigChangeEvent;
import com.virjar.vscrawler.event.systemevent.NewSeedArrivalEvent;
import com.virjar.vscrawler.util.PathResolver;
import com.virjar.vscrawler.util.SingtonObjectHolder;
import com.virjar.vscrawler.util.VSCrawlerConstant;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by virjar on 17/5/14. <br/>
 * 伯克利数据库,用来管理种子任务
 * 
 * @since 0.0.1
 * @author virjar
 */
@Slf4j
public class BerkeleyDBSeedManager implements CrawlerConfigChangeEvent, NewSeedArrivalEvent {

    private Environment env;

    private String dbFilePath;

    public BerkeleyDBSeedManager() {
        // 配置数据库环境
        configEnv();

        // 移植初始种子信息

        // 监听消息
        AutoEventRegistry.getInstance().registerObserver(this);
    }

    private void configEnv() {
        resolveDBFile();

        EnvironmentConfig environmentConfig = new EnvironmentConfig();
        environmentConfig.setAllowCreate(true);
        env = new Environment(new File(dbFilePath), environmentConfig);
    }

    private void migrateInitSeed() {

    }

    private void resolveDBFile() {
        // 配置数据库文件地址
        Properties properties = SingtonObjectHolder.vsCrawlerConfigFileWatcher.loadedProperties();
        String workpath = properties.getProperty(VSCrawlerConstant.VSCRAWLER_WORKING_DIRECTORY, "classpath:work");
        log.info("vsCrawler配置工作目录:{}", workpath);
        workpath = PathResolver.resolveAbsolutePath(workpath);
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
            if (!workFile.mkdirs()) {
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

    }
}
