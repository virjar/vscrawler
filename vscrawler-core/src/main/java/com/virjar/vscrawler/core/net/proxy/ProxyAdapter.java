package com.virjar.vscrawler.core.net.proxy;

import com.virjar.vscrawler.core.net.proxy.Proxy;
import org.apache.http.Header;

import java.util.List;

/**
 * Created by virjar on 2017/11/27.<br/>
 * 大多数请求代理只需要ip&port
 */
public abstract class ProxyAdapter implements Proxy {
    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public List<Header> getAuthenticationHeaders() {
        return null;
    }

    @Override
    public void offline() {

    }

    @Override
    public void block(long blockTimeStamp) {

    }

    @Override
    public void recordUsage() {

    }

    @Override
    public void recordFailed() {

    }

    @Override
    public boolean isDisable() {
        return false;
    }
}
