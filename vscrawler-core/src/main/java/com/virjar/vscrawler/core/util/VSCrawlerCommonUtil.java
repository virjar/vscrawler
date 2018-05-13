package com.virjar.vscrawler.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.VSCrawlerContext;
import com.virjar.vscrawler.core.net.session.CrawlerSession;
import com.virjar.vscrawler.core.seed.Seed;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by virjar on 17/5/16.
 */
public class VSCrawlerCommonUtil {

    private static InheritableThreadLocal<CrawlerSession> crawlerSessionThreadLocal = new InheritableThreadLocal<>();
    private static InheritableThreadLocal<VSCrawlerContext> crawlerContextThreadLocal = new InheritableThreadLocal<>();
    private static InheritableThreadLocal<Long> grabStartTimeStampThreadLocal = new InheritableThreadLocal<>();
    private static InheritableThreadLocal<Long> grabTimeOut = new InheritableThreadLocal<>();

    public static String transferSeedToString(Seed seed) {
        return JSONObject.toJSONString(seed);
    }

    public static Seed transferStringToSeed(String seed) {
        return JSON.toJavaObject(JSONObject.parseObject(seed), Seed.class);
    }

    public static CrawlerSession crawlerSessionInThread() {
        return crawlerSessionThreadLocal.get();
    }

    public static void setCrawlerSession(CrawlerSession crawlerSession) {
        crawlerSessionThreadLocal.set(crawlerSession);
    }

    public static void clearCrawlerSession() {
        crawlerSessionThreadLocal.remove();
    }

    public static void setVSCrawlerContext(VSCrawlerContext vsCrawlerContext) {
        crawlerContextThreadLocal.set(vsCrawlerContext);
    }

    public static VSCrawlerContext getVSCrawlerContext() {
        return crawlerContextThreadLocal.get();
    }

    public static Long getGrabStartTimeStampThreadLocal() {
        return grabStartTimeStampThreadLocal.get();
    }

    public static void setGrabStartTimeStampThreadLocal(Long grabStartTimeStampThreadLocal) {
        VSCrawlerCommonUtil.grabStartTimeStampThreadLocal.set(grabStartTimeStampThreadLocal);
    }

    public static void setGrabTimeOut(Long grabTimeOut) {
        VSCrawlerCommonUtil.grabTimeOut.set(grabTimeOut);
    }

    public static void clearGrabTimeOutControl() {
        grabStartTimeStampThreadLocal.remove();
        grabTimeOut.remove();
    }

    public static boolean hasTimeOut() {
        return grabTimeOut.get() != null && (grabStartTimeStampThreadLocal.get() == null || System.currentTimeMillis() > grabStartTimeStampThreadLocal.get() + grabTimeOut.get());
    }

    public static long grabTaskLessTime() {
        if (grabStartTimeStampThreadLocal.get() == null) {
            return 0;
        }
        if (grabTimeOut.get() == null) {
            return VSCrawlerConstant.defaultSessionRequestTimeOut;
        }
        long ret = grabStartTimeStampThreadLocal.get() + grabTimeOut.get() - System.currentTimeMillis();
        if (ret < 0) {
            ret = 0;
        }
        return ret;
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
        Random random = ThreadLocalRandom.current();
        for (T t : input) {
            if ((random.nextInt() & 0x01) == 1) {
                ret.addFirst(t);
            } else {
                ret.addLast(t);
            }
        }
        return ret;
    }

    public enum JSONStringType {
        // Json 数组
        JSON_ARRAY("0"),
        // Json 对象
        JSON_OBJECT("1");

        public String getValue() {
            return value;
        }

        private String value;

        JSONStringType(String string) {
            this.value = string;
        }
    }
}
