package com.virjar.vscrawler.core.net.proxy;

import com.virjar.dungproxy.client.ippool.IpPoolHolder;
import com.virjar.dungproxy.client.model.AvProxy;

/**
 * Created by virjar on 17/5/9.
 * 
 * @author virjar
 * @since 0.0.1
 */
public class DefaultIPPool implements IPPool {

    @Override
    public Proxy getIP(String host, String accessURL) {
        AvProxy bind = IpPoolHolder.getIpPool().bind(host, accessURL);
        if (bind == null) {
            return null;
        }
        return new VSCrawlerProxy(bind);
    }
}
