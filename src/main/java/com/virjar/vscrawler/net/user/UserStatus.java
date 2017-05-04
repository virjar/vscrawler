package com.virjar.vscrawler.net.user;

/**
 * Created by virjar on 17/4/30.
 * @author virjar
 * @since 0.0.1
 */
public enum UserStatus {
    OK, // 正常状态
    FORBID, // 被封禁
    BLOCK, // 暂时被封禁
    PASSWORDERROR, // 密码错误
    NOTEXIST// 该用户不存在
}
