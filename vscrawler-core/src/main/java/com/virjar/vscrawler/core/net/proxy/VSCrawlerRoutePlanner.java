package com.virjar.vscrawler.core.net.proxy;

import static com.virjar.vscrawler.core.util.VSCrawlerConstant.VSCRAWLER_AVPROXY_KEY;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.conn.DefaultRoutePlanner;
import org.apache.http.protocol.HttpContext;

import com.virjar.dungproxy.client.httpclient.conn.ProxyBindRoutPlanner;
import com.virjar.vscrawler.core.net.proxy.strategy.ProxyPlanner;
import com.virjar.vscrawler.core.net.session.CrawlerSession;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by virjar on 17/4/30.
 * 
 * @author virjar
 * @since 0.0.1
 */

@Slf4j
public class VSCrawlerRoutePlanner extends DefaultRoutePlanner {

    private IPPool ipPool;
    // private ProxyStrategy proxyStrategy;
    private CrawlerSession crawlerSession;
    private ProxyPlanner proxyPlanner;

    public VSCrawlerRoutePlanner(ProxyBindRoutPlanner delegate, IPPool ipPool, ProxyPlanner proxyPlanner,
            CrawlerSession crawlerSession) {
        super(delegate.getSchemePortResolver());
        this.ipPool = ipPool;
        // this.proxyStrategy = proxyStrategy;
        this.proxyPlanner = proxyPlanner;
        this.crawlerSession = crawlerSession;
    }

    @Override
    protected HttpHost determineProxy(HttpHost host, HttpRequest request, HttpContext context) throws HttpException {
        HttpClientContext httpClientContext = HttpClientContext.adapt(context);
        Proxy proxy = proxyPlanner.determineProxy(host, request, context, ipPool, crawlerSession);

        if (proxy == null) {
            return null;
        }
        log.info("{} 当前使用IP为:{}:{}", host.getHostName(), proxy.getIp(), proxy.getPort());
        context.setAttribute(VSCRAWLER_AVPROXY_KEY, proxy);

        if (proxy.getAuthenticationHeaders() != null) {
            for (Header header : proxy.getAuthenticationHeaders()) {
                request.addHeader(header);
            }
        }

        if (StringUtils.isNotEmpty(proxy.getUsername()) && StringUtils.isNotEmpty(proxy.getPassword())) {
            BasicCredentialsProvider credsProvider1 = new BasicCredentialsProvider();
            httpClientContext.setCredentialsProvider(credsProvider1);
            credsProvider1.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(proxy.getUsername(), proxy.getPassword()));
        }
        return new HttpHost(proxy.getIp(), proxy.getPort());
    }
}
