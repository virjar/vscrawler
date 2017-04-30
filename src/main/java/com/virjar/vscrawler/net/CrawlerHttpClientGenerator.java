package com.virjar.vscrawler.net;

import com.virjar.dungproxy.client.httpclient.CrawlerHttpClient;
import org.apache.http.client.CookieStore;

/**
 * Created by virjar on 17/4/30.
 */
public interface CrawlerHttpClientGenerator {
    CrawlerHttpClient gen(CookieStore cookieStore);
}
