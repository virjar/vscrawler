package com.virjar.vscrawler.core.net.user;

import com.alibaba.fastjson.JSONObject;
import com.virjar.vscrawler.core.VSCrawlerContext;
import com.virjar.vscrawler.core.event.systemevent.SessionCreateEvent;
import com.virjar.vscrawler.core.event.systemevent.SessionDestroyEvent;
import com.virjar.vscrawler.core.net.session.CrawlerSession;
import com.virjar.vscrawler.core.net.session.LoginHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by virjar on 17/6/3. 考虑之后,决定使用插件的形式注入自动登录功能
 *
 * @author virjar
 * @since 0.0.1
 */
@Slf4j
public class AutoLoginPlugin implements SessionCreateEvent, SessionDestroyEvent {
    private IUserManager userManager;
    private LoginHandler loginHandler;

    public AutoLoginPlugin(LoginHandler loginHandler, IUserManager userManager) {
        this.loginHandler = loginHandler;
        this.userManager = userManager;
    }

//    @Override
//    public void onCrawlerStart(VSCrawler vsCrawler) {
//        vsCrawler.getVsCrawlerContext().getAutoEventRegistry().registerObserver(this);
//    }

    @Override
    public void onSessionCreateEvent(VSCrawlerContext vsCrawlerContext, CrawlerSession crawlerSession) {
        // 其他插件已经把这个session判定为无效,不需要走登录流程
        if (!crawlerSession.isValid()) {
            return;
        }

        User user = userManager.allocateUser();
        if (user == null) {
            log.warn("不能分发账户数据");
            crawlerSession.setValid(false);
            return;
        }
        UserStatus userStatus = user.getUserStatus();
        try {
            boolean loginSuccess = loginHandler.onLogin(user, crawlerSession.getCookieStore(),
                    crawlerSession.getCrawlerHttpClient());
            if (loginSuccess) {
                UserUtil.setUser(crawlerSession, user);
                return;
            }
        } catch (Exception e) {
            log.error("登录发生异常", e);
        }
        crawlerSession.setValid(false);
        if (user.getUserStatus() == userStatus) {
            user.setUserStatus(UserStatus.BLOCK);
        }
        userManager.returnUser(user);
        log.warn("用户:{} 登录失败", JSONObject.toJSONString(user));
    }

    @Override
    public void onSessionDestroy(VSCrawlerContext vsCrawlerContext, CrawlerSession crawlerSession) {
        User user = UserUtil.getUser(crawlerSession);
        if (user == null) {
            return;
        }
        if (!crawlerSession.isValid()) {
            user.setUserStatus(UserStatus.BLOCK);
        }
        userManager.returnUser(user);
    }
}
