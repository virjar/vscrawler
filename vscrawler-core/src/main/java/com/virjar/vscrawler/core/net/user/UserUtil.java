package com.virjar.vscrawler.core.net.user;

import com.virjar.vscrawler.core.net.session.CrawlerSession;
import com.virjar.vscrawler.core.util.VSCrawlerConstant;

/**
 * Created by virjar on 17/6/4.
 * 
 * @since 0.0.1
 * @author virjar
 */
public class UserUtil {
    public static User getUser(CrawlerSession crawlerSession) {
        Object extInfo = crawlerSession.getExtInfo(VSCrawlerConstant.SEEION_POOL_USER_KEY);
        if (extInfo instanceof User) {
            return (User) extInfo;
        }
        return null;
    }

    public static void setUser(CrawlerSession crawlerSession, User user) {
        crawlerSession.setExtInfo(VSCrawlerConstant.SEEION_POOL_USER_KEY, user);
    }
}
