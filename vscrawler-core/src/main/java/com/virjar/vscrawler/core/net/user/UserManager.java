package com.virjar.vscrawler.core.net.user;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.virjar.vscrawler.core.event.support.AutoEventRegistry;
import com.virjar.vscrawler.core.event.systemevent.UserStateChangeEvent;

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

    private LinkedList<User> idleUsers = Lists.newLinkedList();

    private Set<User> blockUsers = Sets.newHashSet();

    public UserManager(UserResourceFacade userResourceFacade) {
        if (userResourceFacade == null) {
            userResourceFacade = new DefaultUserResource();
        }
        this.userResourceFacade = userResourceFacade;
        AutoEventRegistry.getInstance().registerObserver(this);
    }

    @Override
    public void userStatusChange(User user, UserStatus originStatus, UserStatus newStatus) {
        for (User tempUser : allUser) {
            if (tempUser.equals(user)) {// 虽然equal,但是可能不是同一个对象
                tempUser.setUserStatus(newStatus);
            }
        }
    }

    /**
     * recycle user resource to user resources pool, must make user instance detach from session
     * 
     * @param user user instance
     */
    public void returnUser(User user) {
        UserStatus userStatus = user.getUserStatus();
        if (userStatus != UserStatus.OK) {
            blockUsers.add(user);
        } else {
            idleUsers.add(user);
        }
    }

    public User allocateUser() {
        User poll = idleUsers.poll();
        if (poll == null) {
            unblock();
            poll = idleUsers.poll();
        }

        if (poll == null) {
            Collection<User> users = userResourceFacade.importUser();
            if (users != null) {
                for (User user : users) {
                    if (!allUser.contains(user)) {
                        allUser.add(user);
                        idleUsers.offer(user);
                    }
                }
                poll = idleUsers.poll();
            }
        }

        while (poll != null) {
            if (poll.getUserStatus() != UserStatus.OK && poll.getUserStatus() != UserStatus.INIT) {
                blockUsers.add(poll);
                poll = idleUsers.poll();
            } else {
                return poll;
            }
        }

        return null;
    }

    private synchronized void unblock() {
        // TODO sync 优化
        Iterator<User> iterator = blockUsers.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if (user.getUserStatus() == UserStatus.INIT || user.getUserStatus() == UserStatus.OK) {
                iterator.remove();
                idleUsers.add(user);
            }
        }
    }

}
