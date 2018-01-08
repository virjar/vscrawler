package com.virjar.vscrawler.core.net.user;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Created by virjar on 17/4/14.
 *
 * @author virjar
 * @since 0.0.1
 */
@Getter
@Setter
public class User {
    private String userID;
    private String password;
    private String phone;
    private String email;
    private boolean isValid;
    private Map<String, Object> extInfo = Maps.newHashMap();
    private UserStatus userStatus = UserStatus.OK;


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
