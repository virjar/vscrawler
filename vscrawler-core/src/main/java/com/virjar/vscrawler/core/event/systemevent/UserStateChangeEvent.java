package com.virjar.vscrawler.core.event.systemevent;

import com.virjar.vscrawler.core.VSCrawlerContext;
import com.virjar.vscrawler.core.event.support.AutoEvent;
import com.virjar.vscrawler.core.net.user.User;
import com.virjar.vscrawler.core.net.user.UserStatus;

/**
 * Created by virjar on 17/4/30.
 *
 * @author virjar
 * @since 0.0.1
 */
public interface UserStateChangeEvent {
    @AutoEvent
    void userStatusChange(VSCrawlerContext vsCrawlerContext, User user, UserStatus originStatus, UserStatus newStatus);
}
