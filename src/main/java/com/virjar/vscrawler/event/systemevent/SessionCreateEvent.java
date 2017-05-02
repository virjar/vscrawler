package com.virjar.vscrawler.event.systemevent;

import com.virjar.vscrawler.event.support.AutoEvent;
import com.virjar.vscrawler.net.user.User;

/**
 * Created by virjar on 17/4/30.
 */
public interface SessionCreateEvent {
    @AutoEvent
    void onSessionCreateEvent(User user);
}
