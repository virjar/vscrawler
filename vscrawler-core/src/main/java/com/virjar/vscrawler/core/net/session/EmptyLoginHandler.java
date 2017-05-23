package com.virjar.vscrawler.core.net.session;

import com.virjar.vscrawler.core.net.user.User;
import org.apache.http.client.CookieStore;

import com.virjar.dungproxy.client.httpclient.CrawlerHttpClient;

/**
 * Created by virjar on 17/4/16.
 * @author virjar
 * @since 0.0.1
 */
public class EmptyLoginHandler implements LoginHandler {
    @Override
    public boolean onLogin(User user, CookieStore cookieStore, CrawlerHttpClient crawlerHttpClient) {
        return true;
    }

    @Override
    public boolean testLogin(CookieStore cookieStore, CrawlerHttpClient crawlerHttpClient) {
        return true;
    }

    @Override
    public void logout(User user, CookieStore cookieStore, CrawlerHttpClient crawlerHttpClient) {

    }
}
