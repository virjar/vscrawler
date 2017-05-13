package com.virjar.vscrawler.net.proxy;

import java.util.List;

import org.apache.http.Header;

import com.virjar.dungproxy.client.model.AvProxy;

/**
 * Created by virjar on 17/5/9.
 */
public class VSCrawlerProxy implements Proxy {
    private AvProxy avProxy;

    public VSCrawlerProxy(AvProxy avProxy) {
        this.avProxy = avProxy;
    }

    @Override
    public String getIp() {
        return avProxy.getIp();
    }

    @Override
    public Integer getPort() {
        return avProxy.getPort();
    }

    @Override
    public String getUsername() {
        return avProxy.getUsername();
    }

    @Override
    public String getPassword() {
        return avProxy.getPassword();
    }

    @Override
    public List<Header> getAuthenticationHeaders() {
        return avProxy.getAuthenticationHeaders();
    }

    @Override
    public void offline() {
        avProxy.offline();
    }

    @Override
    public void block(long blockTimeStamp) {
        avProxy.block(blockTimeStamp);
    }

    @Override
    public void recordUsage() {
        avProxy.recordUsage();
    }

    @Override
    public void recordFailed() {
        avProxy.recordFailed();
    }

    @Override
    public boolean isDisable() {
        return avProxy.isDisable();
    }
}
