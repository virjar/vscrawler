package com.virjar.vscrawler.core.net.proxy.strategy;

import com.virjar.vscrawler.core.net.proxy.IPPool;
import com.virjar.vscrawler.core.net.proxy.Proxy;
import com.virjar.vscrawler.core.net.session.CrawlerSession;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;

/**
 * Created by virjar on 17/5/13.
 */
public class NotProxyPlanner implements ProxyPlanner {
    @Override
    public Proxy determineProxy(HttpHost host, HttpRequest request, HttpContext context, IPPool ipPool, CrawlerSession crawlerSession) {
        return null;
    }
}
