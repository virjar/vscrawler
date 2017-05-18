package com.virjar.vscrawler.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.virjar.vscrawler.seed.Seed;

/**
 * Created by virjar on 17/5/16.
 */
public class VSCrawlerCommonUtil {
    public static boolean closeQuietly(Environment environment) {
        try {
            environment.close();
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
