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
public class EveryRequestPlanner implements ProxyPlanner {
    @Override
    public Proxy determineProxy(HttpHost host, HttpRequest request, HttpContext context, IPPool ipPool,
            CrawlerSession crawlerSession) {
        HttpClientContext httpClientContext = HttpClientContext.adapt(context);

        Proxy bind = (Proxy) context.getAttribute(VSCRAWLER_AVPROXY_KEY);
        String accessUrl = null;
        if (request instanceof HttpRequestWrapper || request instanceof HttpGet) {
            accessUrl = HttpUriRequest.class.cast(request).getURI().toString();
        }
        if (!PoolUtil.isDungProxyEnabled(httpClientContext)) {
            log.info("{}不会被代理", accessUrl);
            return null;
        }

        if (bind == null || bind.isDisable()) {
            bind = ipPool.getIP(host.getHostName(), accessUrl);
        }

        if (bind == null) {
            return null;
        }

        log.info("{} 当前使用IP为:{}:{}", host.getHostName(), bind.getIp(), bind.getPort());
        // 将绑定IP放置到context,用于后置拦截器统计这个IP的使用情况
        return bind;
    }
}
