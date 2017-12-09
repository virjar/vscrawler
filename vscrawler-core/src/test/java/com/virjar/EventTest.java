package com.virjar;

import com.virjar.dungproxy.client.util.CommonUtil;
import com.virjar.vscrawler.core.VSCrawlerContext;
import com.virjar.vscrawler.core.event.EventLoop;
import com.virjar.vscrawler.core.event.support.AutoEventRegistry;
import com.virjar.vscrawler.core.event.systemevent.UserLoginEvent;
import com.virjar.vscrawler.core.net.user.User;

/**
 * Created by virjar on 17/5/1.
 */
public class EventTest {
    public static void main(String[] args) {
        VSCrawlerContext vsCrawlerContext = VSCrawlerContext.create("testCrawler");

        vsCrawlerContext.getEventLoop().loop();

        AutoEventRegistry eventRegister = vsCrawlerContext.getAutoEventRegistry();

        eventRegister.registerObserver(new UserLoginEvent() {
            @Override
            public void afterUserLogin(VSCrawlerContext vsCrawlerContext1, User user, boolean loginSucces) {
                System.out.println(Thread.currentThread() + "用户登录:" + (loginSucces ? "成功" : "失败"));
            }
        });

        UserLoginEvent userLoginEvent = eventRegister.findEventDeclaring(UserLoginEvent.class);
        for (int i = 0; i < 10; i++) {
            userLoginEvent.afterUserLogin(vsCrawlerContext, null, false);
        }

        CommonUtil.sleep(20000);
    }
}
