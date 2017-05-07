package com.virjar.vscrawler.net.proxy;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.DefaultRoutePlanner;
import org.apache.http.protocol.HttpContext;

import com.virjar.dungproxy.client.httpclient.conn.ProxyBindRoutPlanner;
import com.virjar.vscrawler.net.session.CrawlerSession;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by virjar on 17/4/30.
 * 
 * @author virjar
 * @since 0.0.1
 */

@Slf4j
public class VSCrawlerRoutePlanner extends DefaultRoutePlanner {

    private ProxyBindRoutPlanner delegate;
    private ProxyStrategy proxyStrategy;
    private CrawlerSession crawlerSession;
    private AvProxyRoutePlanner avProxyRoutePlanner;

    public VSCrawlerRoutePlanner(ProxyBindRoutPlanner delegate, ProxyStrategy proxyStrategy,
            CrawlerSession crawlerSession) {
        super(delegate.getSchemePortResolver());
        this.delegate = delegate;
        this.proxyStrategy = proxyStrategy;
        this.crawlerSession = crawlerSession;
        this.avProxyRoutePlanner = new AvProxyRoutePlanner(crawlerSession);
    }

    @Override
    public HttpRoute determineRoute(final HttpHost host, final HttpRequest request, final HttpContext context)
            throws HttpException {
        if (proxyStrategy == ProxyStrategy.REQUEST) {// dungproxy默认方案是每次换代理
            return delegate.determineRoute(host, request, context);
        }
        if (proxyStrategy == ProxyStrategy.SESSION || proxyStrategy == ProxyStrategy.USER) {
            return avProxyRoutePlanner.determineRoute(host, request, context);
        }
        return null;
    }
}
