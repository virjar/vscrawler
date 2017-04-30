package com.virjar.vscrawler.net.proxy;

import com.virjar.dungproxy.client.model.AvProxy;

/**
 * Created by virjar on 17/4/30.
 */
public interface ProxyIpPool {
    AvProxy borwProxy(String host, String accessURL);
}
