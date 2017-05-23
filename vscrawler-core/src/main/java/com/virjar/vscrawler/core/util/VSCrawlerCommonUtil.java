package com.virjar.vscrawler.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.virjar.vscrawler.core.seed.Seed;

/**
 * Created by virjar on 17/5/16.
 */
public class VSCrawlerCommonUtil {
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
}
