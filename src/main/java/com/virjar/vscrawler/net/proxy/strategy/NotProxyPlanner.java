package com.virjar.vscrawler.net.proxy.strategy;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;

import com.virjar.vscrawler.net.proxy.IPPool;
import com.virjar.vscrawler.net.proxy.Proxy;
import com.virjar.vscrawler.net.session.CrawlerSession;

/**
 * Created by virjar on 17/5/13.
 */
public class NotProxyPlanner implements ProxyPlanner {
    @Override
    public Proxy determineProxy(HttpHost host, HttpRequest request, HttpContext context, IPPool ipPool, CrawlerSession crawlerSession) {
        return null;
    }
}
