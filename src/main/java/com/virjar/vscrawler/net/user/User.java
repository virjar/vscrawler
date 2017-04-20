package com.virjar.vscrawler.net.user;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.virjar.vscrawler.net.session.CrawlerSession;

/**
 * Created by virjar on 17/4/14.
 */
public class User {
    private String userID;
    private String password;
    private String phone;
    private String email;
    private boolean isValid;
    private Map<String, String> extInfo;

    private AtomicReference<CrawlerSession> nowSession = new AtomicReference<>();

    public void holdUser(CrawlerSession crawlerSession) {
        nowSession.set(crawlerSession);
    }

    public boolean checkHold(CrawlerSession crawlerSession) {
        return nowSession.get() == crawlerSession;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Map<String, String> getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(Map<String, String> extInfo) {
        this.extInfo = extInfo;
    }
}
