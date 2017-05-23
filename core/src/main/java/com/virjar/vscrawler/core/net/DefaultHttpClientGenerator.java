package com.virjar.vscrawler.core.net;

import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.LaxRedirectStrategy;

import com.virjar.dungproxy.client.httpclient.CrawlerHttpClient;
import com.virjar.dungproxy.client.httpclient.CrawlerHttpClientBuilder;
import com.virjar.dungproxy.client.ippool.config.ProxyConstant;

/**
 * Created by virjar on 17/4/30.
 * 
 * @author virjar
 * @since 0.0.1
 */
public class DefaultHttpClientGenerator implements CrawlerHttpClientGenerator {
    @Override
    public CrawlerHttpClient gen(CrawlerHttpClientBuilder proxyFeedBackDecorateHttpClientBuilder) {
        SocketConfig socketConfig = SocketConfig.custom().setSoKeepAlive(true).setSoLinger(-1).setSoReuseAddress(false)
                .setSoTimeout(ProxyConstant.SOCKETSO_TIMEOUT).setTcpNoDelay(true).build();

        return proxyFeedBackDecorateHttpClientBuilder.setMaxConnTotal(100).setMaxConnPerRoute(50)
                .setDefaultSocketConfig(socketConfig)
                // .setSSLSocketFactory(sslConnectionSocketFactory)
                // dungproxy0.0.6之后的版本,默认忽略https证书检查
                .setRedirectStrategy(new LaxRedirectStrategy()).build();

    }
}
