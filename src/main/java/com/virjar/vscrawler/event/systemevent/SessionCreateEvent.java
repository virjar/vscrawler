package com.virjar.vscrawler.event.systemevent;

import com.virjar.vscrawler.event.Event;
import com.virjar.vscrawler.net.user.User;

/**
 * Created by virjar on 17/4/30.
 */
public class SessionCreateEvent extends Event {
    public SessionCreateEvent(User user) {
        super("SessionCreate");
        setData(user);
    }
}
