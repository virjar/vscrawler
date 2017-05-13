package com.virjar.vscrawler.net.session;

import org.apache.http.impl.execchain.ClientExecChain;

import com.virjar.dungproxy.client.httpclient.CrawlerHttpClientBuilder;

/**
 * Created by virjar on 17/5/13.
 */
public class ProxyFeedBackDecorateHttpClientBuilder extends CrawlerHttpClientBuilder {
    @Override
    protected ClientExecChain decorateProtocolExec(ClientExecChain protocolExec) {
        return new ProxyFeedBackClientExecChain(protocolExec);
    }
}
