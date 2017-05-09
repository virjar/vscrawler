package com.virjar.vscrawler.net.proxy;

import static com.virjar.vscrawler.util.VSCrawlerConstant.VSCRAWLER_AVPROXY_KEY;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.conn.DefaultRoutePlanner;
import org.apache.http.protocol.HttpContext;

import com.virjar.dungproxy.client.ippool.IpPoolHolder;
import com.virjar.dungproxy.client.model.AvProxy;
import com.virjar.dungproxy.client.util.PoolUtil;
import com.virjar.vscrawler.net.session.CrawlerSession;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by virjar on 17/5/7.
 */
@Slf4j
public class AvProxyRoutePlanner extends DefaultRoutePlanner {
    private CrawlerSession crawlerSession;

    public AvProxyRoutePlanner(CrawlerSession crawlerSession) {
        super(null);
        this.crawlerSession = crawlerSession;
    }

    @Override
    protected HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
        HttpClientContext httpClientContext = HttpClientContext.adapt(context);

        AvProxy avproxy = (AvProxy) crawlerSession.getExt().get(VSCRAWLER_AVPROXY_KEY);
        if (avproxy == null) {
            String accessUrl = null;
            if (request instanceof HttpRequestWrapper || request instanceof HttpGet) {
                accessUrl = HttpUriRequest.class.cast(request).getURI().toString();
            }
            if (!PoolUtil.isDungProxyEnabled(httpClientContext)) {
                log.info("{}不会被代理", accessUrl);
                return null;
            }
            avproxy = IpPoolHolder.getIpPool().bind(target.getHostName(), accessUrl);
            if (avproxy == null) {
                return null;
            }
            crawlerSession.getExt().put(VSCRAWLER_AVPROXY_KEY, avproxy);
        }

        log.info("{} 当前使用IP为:{}:{}", target.getHostName(), avproxy.getIp(), avproxy.getPort());
        avproxy.recordUsage();
        // context.setAttribute("USED_PROXY_KEY", avproxy);
        if (avproxy.getAuthenticationHeaders() != null) {
            for (Header header : avproxy.getAuthenticationHeaders()) {
                request.addHeader(header);
            }
        }

        if (StringUtils.isNotEmpty(avproxy.getUsername()) && StringUtils.isNotEmpty(avproxy.getPassword())) {
            BasicCredentialsProvider credsProvider1 = new BasicCredentialsProvider();
            httpClientContext.setCredentialsProvider(credsProvider1);
            credsProvider1.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(avproxy.getUsername(), avproxy.getPassword()));
        }

        return new HttpHost(avproxy.getIp(), avproxy.getPort());
    }
}
