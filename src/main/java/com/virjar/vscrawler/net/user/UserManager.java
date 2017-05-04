package com.virjar.vscrawler.net.user;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Created by virjar on 17/5/4. <br/>
 * 管理多个用户
 *
 * @author virjar
 * @since 0.0.1
 */
public class UserManager {
    private UserResourceFacade userResourceFacade;

    private List<User> allUser = Lists.newArrayList();

    private List<User> idleUsers = Lists.newArrayList();
}
