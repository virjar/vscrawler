package com.virjar.vscrawler.core.log;

import java.util.UUID;

/**
 * Created by virjar on 2018/2/10.<br>
 *
 * @author virjar
 * @since 0.3.0
 */
public class LogIdGenerator {
    public static String genGrabTransactionID(String crawlerName) {
        return crawlerName + "-" + UUID.randomUUID().toString();
    }
}
