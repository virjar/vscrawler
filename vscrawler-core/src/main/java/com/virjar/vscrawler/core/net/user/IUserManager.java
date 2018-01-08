package com.virjar.vscrawler.core.net.user;

/**
 * Created by virjar on 2018/1/8.
 */
public interface IUserManager {
    void returnUser(User user);

    User allocateUser();
}
