package com.virjar.vscrawler.core.event.systemevent;

import com.virjar.vscrawler.core.event.support.AutoEvent;
import com.virjar.vscrawler.core.net.user.User;

/**
 * Created by virjar on 17/4/30.
 * @author virjar
 * @since 0.0.1
 */
public interface UserLoginEvent {
    @AutoEvent
    void afterUserLogin(User user, boolean loginSucces);
}
