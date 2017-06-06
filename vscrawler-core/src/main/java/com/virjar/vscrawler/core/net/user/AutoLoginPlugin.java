package com.virjar.vscrawler.core.net.user;

import com.virjar.vscrawler.core.VSCrawler;
import com.virjar.vscrawler.core.event.support.AutoEventRegistry;
import com.virjar.vscrawler.core.event.systemevent.SessionCreateEvent;
import com.virjar.vscrawler.core.event.systemevent.SessionDestroyEvent;
import com.virjar.vscrawler.core.net.session.CrawlerSession;
import com.virjar.vscrawler.core.net.session.LoginHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by virjar on 17/6/3. 考虑之后,决定使用插件的形式注入自动登录功能
 * 
 * @since 0.0.1
 * @author virjar
 */
@Slf4j
public class AutoLoginPlugin implements VSCrawler.CrawlerStartCallBack, SessionCreateEvent, SessionDestroyEvent {
    private UserManager userManager;
    private LoginHandler loginHandler;

    public AutoLoginPlugin(LoginHandler loginHandler, UserManager userManager) {
        this.loginHandler = loginHandler;
        this.userManager = userManager;
    }

    @Override
    public void onCrawlerStart(VSCrawler vsCrawler) {
        AutoEventRegistry.getInstance().registerObserver(this);
    }

    @Override
    public void onSessionCreateEvent(CrawlerSession crawlerSession) {
        // 其他插件已经把这个session判定为无效,不需要走登录流程
        if (!crawlerSession.isValid()) {
            return;
        }
        User user = userManager.allocateUser();
        if (user == null) {
            log.error("can not allocate user resource");
            crawlerSession.setValid(false);
            return;
        }
        boolean loginSuccess = loginHandler.onLogin(user, crawlerSession.getCookieStore(),
                crawlerSession.getCrawlerHttpClient());
        if (loginSuccess) {
            UserUtil.setUser(crawlerSession, user);
        } else {
            userManager.returnUser(user);
            log.warn("用户:{} 登录失败", user);
        }
    }

    @Override
    public void onSessionDestroy(CrawlerSession crawlerSession) {
        User user = UserUtil.getUser(crawlerSession);
        if (user != null) {
            userManager.returnUser(user);
        }
    }
}