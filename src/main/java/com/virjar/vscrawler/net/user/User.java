package com.virjar.vscrawler.net.user;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.virjar.vscrawler.net.session.CrawlerSession;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by virjar on 17/4/14.
 * 
 * @author virjar
 * @since 0.0.1
 */
public class User {
    @Getter
    @Setter
    private String userID;
    @Getter
    @Setter
    private String password;
    @Getter
    @Setter
    private String phone;
    @Getter
    @Setter
    private String email;
    @Getter
    @Setter
    private boolean isValid;
    @Getter
    @Setter
    private Map<String, Object> extInfo;

    private AtomicReference<CrawlerSession> nowSession = new AtomicReference<>();

    @Getter
    @Setter
    private UserStatus userStatus = UserStatus.OK;

    public void holdUser(CrawlerSession crawlerSession) {
        nowSession.set(crawlerSession);
    }

    public boolean checkHold(CrawlerSession crawlerSession) {
        return nowSession.get() == crawlerSession;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        User user = (User) o;

        return getUserID().equals(user.getUserID());

    }

    @Override
    public int hashCode() {
        return getUserID().hashCode();
    }
}
