package com.virjar;

import com.virjar.dungproxy.client.util.CommonUtil;
import com.virjar.vscrawler.event.EventLoop;
import com.virjar.vscrawler.event.support.AutoEventRegister;
import com.virjar.vscrawler.event.systemevent.UserLoginEvent;
import com.virjar.vscrawler.net.user.User;

/**
 * Created by virjar on 17/5/1.
 */
public class EventTest {
    public static void main(String[] args) {
        EventLoop.getInstance().loop();

        AutoEventRegister eventRegister = AutoEventRegister.getInstance();

        eventRegister.registerObserver(new UserLoginEvent() {
            @Override
            public void afterUserLogin(User user, boolean loginSucces) {
                System.out.println("用户登录:" + (loginSucces ? "成功" : "失败"));
            }
        });

        UserLoginEvent userLoginEvent = eventRegister.findEventDeclaring(UserLoginEvent.class);
        for (int i = 0; i < 10; i++) {
            userLoginEvent.afterUserLogin(null, false);
        }

        CommonUtil.sleep(20000);
    }
}
