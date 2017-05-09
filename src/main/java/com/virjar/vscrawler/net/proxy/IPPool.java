package com.virjar.vscrawler.net.proxy;

/**
 * Created by virjar on 17/5/9.
 */
public interface IPPool {
    Proxy getIP(String host, String accessURL);
}
