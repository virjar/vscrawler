package com.virjar.vscrawler.core.event.systemevent;

import com.virjar.vscrawler.core.net.user.UserStatus;
import com.virjar.vscrawler.core.event.support.AutoEvent;
import com.virjar.vscrawler.core.net.user.User;

/**
 * Created by virjar on 17/4/30.
 * @author virjar
 * @since 0.0.1
 */
public interface UserStateChangeEvent {
    @AutoEvent
    void userStatusChange(User user, UserStatus originStatus, UserStatus newStatus);
}
