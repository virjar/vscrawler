package com.virjar.vscrawler.net.proxy;

import static com.virjar.vscrawler.util.VSCrawlerConstant.VSCRAWLER_AVPROXY_KEY;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.conn.DefaultRoutePlanner;
import org.apache.http.protocol.HttpContext;

import com.virjar.dungproxy.client.ippool.IpPoolHolder;
import com.virjar.dungproxy.client.model.AvProxy;
import com.virjar.vscrawler.net.session.CrawlerSession;

/**
 * Created by virjar on 17/5/7.
 */
public class AvProxyRoutePlanner extends DefaultRoutePlanner {
    private CrawlerSession crawlerSession;

    public AvProxyRoutePlanner(CrawlerSession crawlerSession) {
        super(null);
        this.crawlerSession = crawlerSession;
    }

    @Override
    protected HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
        AvProxy avproxy = (AvProxy) crawlerSession.getExt().get(VSCRAWLER_AVPROXY_KEY);
        if (avproxy == null) {
            String accessUrl = null;
            if (request instanceof HttpRequestWrapper || request instanceof HttpGet) {
                accessUrl = HttpUriRequest.class.cast(request).getURI().toString();
            }
            avproxy = IpPoolHolder.getIpPool().bind(target.getHostName(), accessUrl);
            if (avproxy != null) {
                crawlerSession.getExt().put(VSCRAWLER_AVPROXY_KEY, avproxy);
            }

        }

        return super.determineProxy(target, request, context);
    }
}
