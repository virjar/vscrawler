package com.virjar.vscrawler.event.systemevent;

import java.util.Map;

import com.google.common.collect.Maps;
import com.virjar.vscrawler.event.Event;
import com.virjar.vscrawler.net.user.User;

/**
 * Created by virjar on 17/4/30.
 */
public class UserLoginEvent extends Event {
    public UserLoginEvent(User user, boolean loginSucces) {
        super("UserLogin");
        Map<String, Object> data = Maps.newHashMap();
        data.put("user", user);
        data.put("success", loginSucces);
        setData(data);
    }
}
