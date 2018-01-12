package com.virjar.vscrawler.core.util;

/**
 * Created by virjar on 17/5/2.
 *
 * @author virjar
 * @since 0.0.1
 */
public interface VSCrawlerConstant {
    String SESSION_POOL_MAX_IDLE = "sessionPool.maxIdle";
    String SESSION_POOL_MIN_IDLE = "sessionPool.minIdl";
    String SESSION_POOL_MAX_DURATION = "sessionPool.maxDuration";
    String SESSION_POOL_MAX_OCCURS = "sessionPool.maxOccurs";
    String SESSION_POOL_ACTIVE_USER = "sessionPool.activeUser";
    String SEEION_POOL_USER_KEY = "sessionPool.userKEY";

    String SESSION_POOL_MONTOR_THREAD_NUMBER = "sessionPool.monitorThreadNumber";

    String VSCRAWLER_THREAD_NUMBER = "vsCrawler.%s.threadNumber";

    String USER_RESOURCE_USERINFO = "userResource.%s.userInfo";

    String VSCRAWLER_AVPROXY_KEY = "vsCrawler.avProxy.key";

    String VSCRAWLER_WORKING_DIRECTORY = "vsCrawler.Working.directory";

    String VSCRAWLER_INIT_SEED_FILE = "vsCrawler.%s.initSeedFile";

    String DEFAULT_CRAWLER_NAME = "vsCrawler";

    String VSCRAWLER_SEED_MANAGER_EXPECTED_SEED_NUMBER = "seedManager.expectedSeedNumber";

    Long defaultSessionRequestTimeOut = 2000L;
}
