package com.virjar.vscrawler.net.proxy;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.conn.DefaultRoutePlanner;
import org.apache.http.protocol.HttpContext;

import com.virjar.dungproxy.client.ippool.config.ProxyConstant;
import com.virjar.dungproxy.client.model.AvProxy;
import com.virjar.dungproxy.client.util.PoolUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by virjar on 17/4/30.
 */

@Slf4j
public class VSCrawlerRoutePlanner extends DefaultRoutePlanner {
    private ProxyIpPool proxyIpPool;

    public VSCrawlerRoutePlanner(SchemePortResolver schemePortResolver, ProxyIpPool proxyIpPool) {
        super(schemePortResolver);
        this.proxyIpPool = proxyIpPool;
    }

    public VSCrawlerRoutePlanner(ProxyIpPool proxyIpPool) {
        this(null, proxyIpPool);
    }

    @Override
    protected HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
        HttpClientContext httpClientContext = HttpClientContext.adapt(context);

        AvProxy bind = (AvProxy) context.getAttribute(ProxyConstant.USED_PROXY_KEY);
        String accessUrl = null;
        if (request instanceof HttpRequestWrapper || request instanceof HttpGet) {
            accessUrl = HttpUriRequest.class.cast(request).getURI().toString();
        }
        if (!PoolUtil.isDungProxyEnabled(httpClientContext)) {

            log.info("{}不会被代理", accessUrl);
            return null;
        }

        if (bind == null || bind.isDisable()) {
            bind = proxyIpPool.borwProxy(target.getHostName(), accessUrl);
        }

        if (bind == null) {
            return null;
        }

        log.info("{} 当前使用IP为:{}:{}", target.getHostName(), bind.getIp(), bind.getPort());
        bind.recordUsage();
        // 将绑定IP放置到context,用于后置拦截器统计这个IP的使用情况
        context.setAttribute(ProxyConstant.USED_PROXY_KEY, bind);

        // 如果代理有认证头部,则注入认证头部
        if (bind.getAuthenticationHeaders() != null) {
            for (Header header : bind.getAuthenticationHeaders()) {
                request.addHeader(header);
            }
        }

        // 注入用户名密码
        if (StringUtils.isNotEmpty(bind.getUsername()) && StringUtils.isNotEmpty(bind.getPassword())) {
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            httpClientContext.setCredentialsProvider(credsProvider);// 强行覆盖,避免并发问题
            credsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(bind.getUsername(), bind.getPassword()));
        }
        return new HttpHost(bind.getIp(), bind.getPort());
    }
}
