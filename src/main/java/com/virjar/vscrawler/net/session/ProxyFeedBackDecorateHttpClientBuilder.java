package com.virjar.vscrawler.net.session;

import org.apache.http.impl.execchain.ClientExecChain;

import com.virjar.dungproxy.client.httpclient.CrawlerHttpClient;
import com.virjar.dungproxy.client.httpclient.CrawlerHttpClientBuilder;

import lombok.Getter;

/**
 * Created by virjar on 17/5/13.
 */
public class ProxyFeedBackDecorateHttpClientBuilder extends CrawlerHttpClientBuilder {
    @Getter
    private boolean build = false;

    @Override
    protected ClientExecChain decorateProtocolExec(ClientExecChain protocolExec) {
        return new ProxyFeedBackClientExecChain(protocolExec);
    }

    @Override
    public CrawlerHttpClient build() {
        build = true;
        return super.build();
    }

}
