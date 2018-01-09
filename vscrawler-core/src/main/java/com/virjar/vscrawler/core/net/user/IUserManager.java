package com.virjar.vscrawler.core.net.user;

/**
 * Created by virjar on 2018/1/8.<br/>
 * 抽象,满足老版本的UserManger和新版本的UserManger的兼容
 *
 * @author virjar
 * @since 0.2.2
 */
public interface IUserManager {
    void returnUser(User user);

    User allocateUser();
}
