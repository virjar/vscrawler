package com.virjar.vscrawler.net.proxy;

import com.virjar.dungproxy.client.ippool.IpPool;
import com.virjar.dungproxy.client.model.AvProxy;

/**
 * Created by virjar on 17/4/30.
 */
public class DefaultIpPool implements ProxyIpPool {
    private IpPool ipPool = IpPool.getInstance();

    @Override
    public AvProxy borwProxy(String host, String accessURL) {
        return ipPool.bind(host, accessURL);
    }
}
