package com.virjar.vscrawler.core.net.session;

import org.apache.http.client.CookieStore;

import com.virjar.dungproxy.client.httpclient.CrawlerHttpClient;
import com.virjar.vscrawler.core.net.user.User;

/**
 * Created by virjar on 17/4/15.
 * 
 * @author virjar
 * @since 0.0.1
 */
public interface LoginHandler {
    /**
     * 登录
     * 
     * @param user 账户信息
     * @param cookieStore 对应cookieStore,登录成功后需要把cookie写到cookieStore
     * @param crawlerHttpClient 这个httpclient持有cookieStore,可以用它方便的登录
     * @return 是否登录
     */
    boolean onLogin(User user, CookieStore cookieStore, CrawlerHttpClient crawlerHttpClient);

    /**
     * 如果长时间没有抓取,测试一下当前账户时候仍然处于登录状态
     * 
     * @param cookieStore
     * @param crawlerHttpClient
     * @return
     */
    boolean testLogin(CookieStore cookieStore, CrawlerHttpClient crawlerHttpClient);

    // TODO
    void logout(User user, CookieStore cookieStore, CrawlerHttpClient crawlerHttpClient);
}
