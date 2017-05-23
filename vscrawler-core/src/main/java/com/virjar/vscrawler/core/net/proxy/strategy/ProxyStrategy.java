package com.virjar.vscrawler.core.net.proxy.strategy;

/**
 * Created by virjar on 17/4/30.
 * 
 * @author virjar
 * @since 0.0.1
 */
public enum ProxyStrategy {

    REQUEST, // 每次都换IP
    SESSION, // session使用的时候换IP
    USER, // 每个用户登录的事情确定代理IP
    NONE, // 不代理
    CUSTOM// 自定义,这种情况必须传递自己的代理规则路由器
}
