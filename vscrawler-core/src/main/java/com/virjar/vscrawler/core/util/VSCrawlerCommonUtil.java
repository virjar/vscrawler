package com.virjar.vscrawler.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.virjar.vscrawler.core.net.session.CrawlerSession;
import com.virjar.vscrawler.core.seed.Seed;

import java.util.*;

/**
 * Created by virjar on 17/5/16.
 */
public class VSCrawlerCommonUtil {

    private static InheritableThreadLocal<CrawlerSession> threadLocal = new InheritableThreadLocal<>();

    public static boolean closeQuietly(Environment environment) {
        if (environment == null) {
            return false;
        }
        try {
            environment.close();
            return true;
        } catch (DatabaseException e) {
            return false;
        }
    }

    public static boolean closeQuietly(Database database) {
        if (database == null) {
            return false;
        }
        try {
            database.close();
            return true;
        } catch (DatabaseException e) {
            return false;
        }
    }

    public static boolean closeQuietly(Cursor cursor) {
        if (cursor == null) {
            return false;
        }
        try {
            cursor.close();
            return true;
        } catch (DatabaseException e) {
            return false;
        }
    }

    public static String transferSeedToString(Seed seed) {
        return JSONObject.toJSONString(seed);
    }

    public static Seed transferStringToSeed(String seed) {
        return JSON.toJavaObject(JSONObject.parseObject(seed), Seed.class);
    }

    public static CrawlerSession crawlerSessionInThread() {
        return threadLocal.get();
    }

    public static void setCrawlerSession(CrawlerSession crawlerSession) {
        threadLocal.set(crawlerSession);
    }

    public static void clearCrawlerSession() {
        threadLocal.remove();
    }

    /**
     * 对于账户数据,如果有空余,可以尝试打乱顺序,防止每次启动使用相同资源
     *
     * @param input 需要被替换顺序的集合
     * @param <T>   集合元素
     * @return 混淆后的list
     */
    public static <T> List<T> confusionSequence(Collection<T> input) {
        if (input == null) {
            return Collections.emptyList();
        }

        LinkedList<T> ret = Lists.newLinkedList();
        Random random = new Random();
        for (T t : input) {
            if ((random.nextInt() & 0x01) == 1) {
                ret.addFirst(t);
            } else {
                ret.addLast(t);
            }
        }
        return ret;
    }

}
