package com.virjar.vscrawler.net.proxy.strategy;

import static com.virjar.vscrawler.util.VSCrawlerConstant.VSCRAWLER_AVPROXY_KEY;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.HttpContext;

import com.virjar.dungproxy.client.util.PoolUtil;
import com.virjar.vscrawler.net.proxy.IPPool;
import com.virjar.vscrawler.net.proxy.Proxy;
import com.virjar.vscrawler.net.session.CrawlerSession;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by virjar on 17/5/13.
 */
@Slf4j
public class EverySessionPlanner implements ProxyPlanner {
    @Override
    public Proxy determineProxy(HttpHost host, HttpRequest request, HttpContext context, IPPool ipPool,
            CrawlerSession crawlerSession) {
        HttpClientContext httpClientContext = HttpClientContext.adapt(context);

        Proxy proxy = (Proxy) crawlerSession.getExt().get(VSCRAWLER_AVPROXY_KEY);
        if (proxy == null) {
            String accessUrl = null;
            if (request instanceof HttpRequestWrapper || request instanceof HttpGet) {
                accessUrl = HttpUriRequest.class.cast(request).getURI().toString();
            }
            if (!PoolUtil.isDungProxyEnabled(httpClientContext)) {
                log.info("{}不会被代理", accessUrl);
                return null;
            }
            proxy = ipPool.getIP(host.getHostName(), accessUrl);
            if (proxy == null) {
                return null;
            }
            crawlerSession.getExt().put(VSCRAWLER_AVPROXY_KEY, proxy);
        }

        return proxy;
    }
}
