package com.virjar.vscrawler.event.systemevent;

import java.util.Map;

import com.google.common.collect.Maps;
import com.virjar.vscrawler.event.Event;
import com.virjar.vscrawler.event.EventHandler;
import com.virjar.vscrawler.net.user.User;
import com.virjar.vscrawler.net.user.UserStatus;

/**
 * Created by virjar on 17/4/30.
 */
public class UserStateChangeEvent extends Event {
    public UserStateChangeEvent(User user, UserStatus originStatus, UserStatus newStatus) {
        super("UserStateChange");
        Map<String, Object> data = Maps.newHashMap();
        data.put("user", user);
        data.put("originStatus", originStatus);
        data.put("newStatus", newStatus);
    }

    public static abstract class UserStateChangeHandler implements EventHandler {

        @Override
        @SuppressWarnings("unchecked")
        public void handEvent(Event event) {
            Map<String, Object> data = (Map<String, Object>) event.getData();
            Object user = data.get("user");
            Object originStatus = data.get("originStatus");
            Object newStatus = data.get("newStatus");
            userStatusChange((User) user, (UserStatus) originStatus, (UserStatus) newStatus);
        }

        abstract void userStatusChange(User user, UserStatus originStatus, UserStatus newStatus);
    }
}
