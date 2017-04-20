package com.virjar.vscrawler.net.session;

import java.security.cert.X509Certificate;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.CookieStore;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.virjar.dungproxy.client.httpclient.CrawlerHttpClient;
import com.virjar.dungproxy.client.httpclient.CrawlerHttpClientBuilder;
import com.virjar.dungproxy.client.ippool.config.ProxyConstant;
import com.virjar.vscrawler.net.user.User;

/**
 * Created by virjar on 17/4/15. <br/>
 * 一个会话,持有到目标网站的cookie数据
 */
public class CrawlerSession {
    private static final Logger logger = LoggerFactory.getLogger(CrawlerSession.class);
    /**
     * session 持有的用户
     */
    private User user;

    private AtomicBoolean enable = new AtomicBoolean(false);

    private CookieStore cookieStore = new BasicCookieStore();

    private AtomicInteger borrowNumber = new AtomicInteger(0);

    private LoginHandler loginHandler;

    private CrawlerHttpClient crawlerHttpClient;

    private long lastActiveTimeStamp = 0L;

    private long initTimeStamp = 0L;

    private AtomicBoolean isLogin = new AtomicBoolean(false);

    public CrawlerSession(User user, LoginHandler loginHandler) {
        this.loginHandler = loginHandler;
        this.user = user;
        user.holdUser(this);
        crawlerHttpClient = buildHttpClient();
        login();
    }

    public User getUser() {
        return user;
    }

    public boolean login() {
        if (isLogin.compareAndSet(false, true)) {
            try {
                if (!user.checkHold(this)) {
                    logger.warn("当前session持有的用户被其他session占用,本session停止工作");
                    return false;
                }

                boolean ret = loginHandler == null || loginHandler.onLogin(user, cookieStore, crawlerHttpClient);
                logger.info("用户:{} 登录:{}", user.getUserID(), ret ? "成功" : "失败");
                if (ret) {
                    initTimeStamp = lastActiveTimeStamp = System.currentTimeMillis();
                    enable.set(true);
                }
                return ret;
            } finally {
                isLogin.set(false);
            }
        }
        return false;
    }

    public boolean isLogin() {
        return isLogin.get();
    }

    public void recordBorrow() {
        lastActiveTimeStamp = System.currentTimeMillis();
        borrowNumber.incrementAndGet();
    }

    public int borrowTimes() {
        return borrowNumber.get();
    }

    public long getLastActiveTimeStamp() {
        return lastActiveTimeStamp;
    }

    public long getInitTimeStamp() {
        return initTimeStamp;
    }

    public boolean testLoginState() {
        if (loginHandler == null) {
            return true;
        }
        if (!enable.get()) {
            synchronized (this) {
                return enable.get() || login();
            }
        }
        boolean loginSuccess = loginHandler.testLogin(cookieStore, crawlerHttpClient);
        if (loginSuccess) {
            lastActiveTimeStamp = System.currentTimeMillis();
        }
        return loginSuccess;
    }

    /**
     * 归还session
     * 
     * @param isSessionEnabled 当前session是否失效,需要由业务层判断
     */
    public void feedback(boolean isSessionEnabled) {
        if (!isSessionEnabled && !login()) {
            enable.set(false);
        }
        borrowNumber.decrementAndGet();
    }

    public CrawlerHttpClient getCrawlerHttpClient() {
        return crawlerHttpClient;
    }

    public boolean getEnable() {
        return enable.get();
    }

    public LoginHandler getLoginHandler() {
        return loginHandler;
    }

    public void setLoginHandler(LoginHandler loginHandler) {
        this.loginHandler = loginHandler;
    }

    /**
     * 清空session
     */
    public void destory() {
        cookieStore.clear();
        enable.set(false);
    }

    // TODO 重构他

    /**
     * 构建一个持有cookie空间的httpclient
     * 
     * @return HttpClient实例
     */
    private CrawlerHttpClient buildHttpClient() {
        SocketConfig socketConfig = SocketConfig.custom().setSoKeepAlive(true).setSoLinger(-1).setSoReuseAddress(false)
                .setSoTimeout(ProxyConstant.SOCKETSO_TIMEOUT).setTcpNoDelay(true).build();
        X509TrustManager x509mgr = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] xcs, String string) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] xcs, String string) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] { x509mgr }, null);
        } catch (Exception e) {
            //// TODO: 16/11/23
        }

        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext,
                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        return CrawlerHttpClientBuilder.create().setMaxConnTotal(1000).setMaxConnPerRoute(50)
                .setDefaultSocketConfig(socketConfig).setSSLSocketFactory(sslConnectionSocketFactory)
                .setRedirectStrategy(new LaxRedirectStrategy()).setDefaultCookieStore(cookieStore).build();
    }
}
