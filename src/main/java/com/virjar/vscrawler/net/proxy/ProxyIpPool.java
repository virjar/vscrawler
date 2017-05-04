package com.virjar.vscrawler.net.proxy;

import com.virjar.dungproxy.client.model.AvProxy;

/**
 * Created by virjar on 17/4/30.
 * @author virjar
 * @since 0.0.1
 */
public interface ProxyIpPool {
    AvProxy borwProxy(String host, String accessURL);
}
