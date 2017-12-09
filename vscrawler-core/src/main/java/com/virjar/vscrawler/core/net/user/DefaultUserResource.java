package com.virjar.vscrawler.core.net.user;

import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.virjar.vscrawler.core.VSCrawlerContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.virjar.vscrawler.core.event.support.AutoEventRegistry;
import com.virjar.vscrawler.core.event.systemevent.CrawlerConfigChangeEvent;
import com.virjar.vscrawler.core.event.systemevent.UserStateChangeEvent;
import com.virjar.vscrawler.core.util.VSCrawlerConstant;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by virjar on 17/5/6. <br/>
 * <p>
 * <pre>
 * 用户数据加载源,默认有两种策略
 * 1.如果vsCrawler配置文件配置了用户数据,那么加载配置文件中的用户数据,且会实时load新增用户数据
 * 2.如果vsCrawler中没有配置用户数据,那么mock用户数据,产生用户名密码均为空的账户信息
 * </pre>
 *
 * @author virjar
 * @since 0.0.1
 */
@Slf4j
public class DefaultUserResource implements UserResourceFacade, CrawlerConfigChangeEvent {
    private Set<User> userCache = Sets.newHashSet();

    private boolean hasConfig = false;

    private int crawlerThreadNumber = 10;

    private AtomicInteger userMockIndex = new AtomicInteger(0);
    private Splitter userSplitter = Splitter.on(",").omitEmptyStrings().trimResults();
    private Splitter userItemSplitter = Splitter.on(":").omitEmptyStrings().trimResults();
    private String userAccountString = null;

    public DefaultUserResource() {
        //AutoEventRegistry.getInstance().registerObserver(this);
    }

    @Override
    public void configChange(VSCrawlerContext vsCrawlerContext, Properties newProperties) {
        String property = newProperties.getProperty(VSCrawlerConstant.USER_RESOURCE_USERINFO);
        hasConfig = property != null;
        if (property == null) {
            crawlerThreadNumber = NumberUtils
                    .toInt(newProperties.getProperty(VSCrawlerConstant.VSCRAWLER_THREAD_NUMBER));
            return;
        }

        if (property.equals(userAccountString)) {
            return;
        }

        // load new user account
        List<String> allUser = userSplitter.splitToList(property);
        int newUserNum = 0;
        for (String userStr : allUser) {
            User user = new User();
            List<String> strings = userItemSplitter.splitToList(userStr);
            if (strings.size() < 2) {
                log.warn("can log load user info from str:{}", userStr);
                continue;
            }
            user.setUserID(strings.get(0));
            user.setPassword(strings.get(1));
            if (strings.size() >= 3) {
                String status = strings.get(2);
                if (StringUtils.equalsIgnoreCase(status, "false")) {
                    vsCrawlerContext.getAutoEventRegistry().findEventDeclaring(UserStateChangeEvent.class)
                            .userStatusChange(vsCrawlerContext, user, UserStatus.UNKNOWN, UserStatus.FORBID);
                    continue;
                }
            }

            user.setUserStatus(UserStatus.INIT);
            userCache.add(user);
            newUserNum++;
        }
        log.info("load {} new user data", newUserNum);

    }

    @Override
    public synchronized Collection<User> importUser() {
        if (hasConfig) {
            Set<User> ret = userCache;
            userCache = Sets.newHashSet();
            return ret;
        } else {
            Set<User> ret = Sets.newHashSet();
            for (int i = 0; i < crawlerThreadNumber; i++) {
                User user = new User();
                user.setUserID(String.valueOf(userMockIndex.incrementAndGet()));
                ret.add(user);
            }
            return ret;
        }
    }
}
