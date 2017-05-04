package com.virjar.vscrawler.net.session;

import org.apache.http.client.CookieStore;

import com.virjar.dungproxy.client.httpclient.CrawlerHttpClient;
import com.virjar.vscrawler.net.user.User;

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
