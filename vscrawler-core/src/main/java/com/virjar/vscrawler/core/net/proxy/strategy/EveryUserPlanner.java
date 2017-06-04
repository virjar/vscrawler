package com.virjar.vscrawler.core.net.proxy.strategy;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.HttpContext;

import com.virjar.dungproxy.client.util.PoolUtil;
import com.virjar.vscrawler.core.net.proxy.IPPool;
import com.virjar.vscrawler.core.net.proxy.Proxy;
import com.virjar.vscrawler.core.net.session.CrawlerSession;
import com.virjar.vscrawler.core.net.user.User;
import com.virjar.vscrawler.core.net.user.UserUtil;
import com.virjar.vscrawler.core.util.VSCrawlerConstant;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by virjar on 17/5/13.
 */
@Slf4j
public class EveryUserPlanner implements ProxyPlanner {
    @Override
    public Proxy determineProxy(HttpHost host, HttpRequest request, HttpContext context, IPPool ipPool,
            CrawlerSession crawlerSession) {
        HttpClientContext httpClientContext = HttpClientContext.adapt(context);

        User user = UserUtil.getUser(crawlerSession);
        if (user == null) {
            log.warn(
                    "you config proxy strategy by user,but this session has not login with a user,proxy bind will be ignore");
            return null;
        }
        Proxy proxy = (Proxy) user.getExtInfo().get(VSCrawlerConstant.VSCRAWLER_AVPROXY_KEY);
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
            user.getExtInfo().put(VSCrawlerConstant.VSCRAWLER_AVPROXY_KEY, proxy);
        }

        return proxy;
    }
}
