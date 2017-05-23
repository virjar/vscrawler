package com.virjar.vscrawler.core.net.session;

import java.io.IOException;

import com.virjar.vscrawler.core.util.VSCrawlerConstant;
import org.apache.http.HttpException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.execchain.ClientExecChain;

import com.virjar.vscrawler.core.net.proxy.Proxy;

/**
 * Created by virjar on 17/5/13.
 * 
 * @since 0.0.1
 * @author virjar
 */
public class ProxyFeedBackClientExecChain implements ClientExecChain {
    private ClientExecChain delegate;

    public ProxyFeedBackClientExecChain(ClientExecChain delegate) {
        this.delegate = delegate;
    }

    @Override
    public CloseableHttpResponse execute(HttpRoute route, HttpRequestWrapper request, HttpClientContext clientContext,
            HttpExecutionAware execAware) throws IOException, HttpException {
        Proxy proxy = (Proxy) clientContext.getAttribute(VSCrawlerConstant.VSCRAWLER_AVPROXY_KEY);
        if (proxy != null) {
            proxy.recordUsage();
        }
        try {
            return delegate.execute(route, request, clientContext, execAware);
        } catch (IOException ioe) {
            if (proxy != null) {
                proxy.recordFailed();
            }
            throw ioe;
        }
    }
}
