package com.virjar.vscrawler.event.systemevent;

import com.virjar.vscrawler.event.support.AutoEvent;
import com.virjar.vscrawler.net.user.User;
import com.virjar.vscrawler.net.user.UserStatus;

/**
 * Created by virjar on 17/4/30.
 */
public interface UserStateChangeEvent {
    @AutoEvent
    void userStatusChange(User user, UserStatus originStatus, UserStatus newStatus);
}
