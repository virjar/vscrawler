package com.virjar.vscrawler.net.user;

/**
 * Created by virjar on 17/4/30.
 * 
 * @author virjar
 * @since 0.0.1
 */
public enum UserStatus {
    INIT, // 初始化状态,还不知道是啥情况
    OK, // 正常状态
    FORBID, // 被封禁
    BLOCK, // 暂时被封禁
    PASSWORDERROR, // 密码错误
    NOTEXIST,// 该用户不存在
    UNKNOWN//未知状态
}
