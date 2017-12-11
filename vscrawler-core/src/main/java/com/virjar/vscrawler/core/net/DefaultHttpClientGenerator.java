package com.virjar.vscrawler.core.net;

import com.virjar.dungproxy.client.httpclient.CrawlerHttpClient;
import com.virjar.dungproxy.client.httpclient.CrawlerHttpClientBuilder;
import com.virjar.dungproxy.client.ippool.config.ProxyConstant;
import com.virjar.vscrawler.core.net.useragent.UserAgentBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.LaxRedirectStrategy;

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

        return proxyFeedBackDecorateHttpClientBuilder
                .setDefaultSocketConfig(socketConfig)
                // .setSSLSocketFactory(sslConnectionSocketFactory)
                // dungproxy0.0.6之后的版本,默认忽略https证书检查
                .setRedirectStrategy(new LaxRedirectStrategy())
                //注意,这里使用ua生产算法自动产生ua,如果是mobile,可以使用
                // com.virjar.vscrawler.core.net.useragent.UserAgentBuilder.randomAppUserAgent()
                .setUserAgent(UserAgentBuilder.randomUserAgent())
                //对于爬虫来说,连接池没啥卵用,直接禁止掉(因为我们可能创建大量HttpClient,每个HttpClient一个连接池,会把系统socket资源撑爆)
                //测试开80个httpClient抓数据大概一个小时系统就会宕机
                .setConnectionReuseStrategy(NoConnectionReuseStrategy.INSTANCE)
                .build();

    }
}
