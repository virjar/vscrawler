package com.virjar.vscrawler.net.user;

import java.util.Set;

import com.google.common.collect.Sets;
import com.virjar.vscrawler.event.support.AutoEventRegistry;
import com.virjar.vscrawler.event.systemevent.UserStateChangeEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by virjar on 17/5/4. <br/>
 * 管理多个用户
 *
 * @author virjar
 * @since 0.0.1
 */
@Slf4j
public class UserManager implements UserStateChangeEvent {
    private UserResourceFacade userResourceFacade;

    private Set<User> allUser = Sets.newHashSet();

    private Set<User> idleUsers = Sets.newHashSet();

    private Set<User> blockUsers = Sets.newHashSet();

    public UserManager(UserResourceFacade userResourceFacade) {
        this.userResourceFacade = userResourceFacade;
        AutoEventRegistry.getInstance().registerObserver(this);
    }

    public UserManager() {
        this(new DefaultUserResource());
    }

    @Override
    public void userStatusChange(User user, UserStatus originStatus, UserStatus newStatus) {
        for (User tempUser : allUser) {
            if (tempUser.equals(user)) {// 虽然equal,但是可能不是同一个对象
                tempUser.setUserStatus(newStatus);
            }
        }
    }


}
