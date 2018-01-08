package com.virjar.vscrawler.core.net.user;

import com.alibaba.fastjson.JSONObject;
import com.virjar.vscrawler.core.resourcemanager.model.ResourceItem;
import com.virjar.vscrawler.core.resourcemanager.service.ResourceLoader;

import java.util.Collection;

/**
 * Created by virjar on 2018/1/8.<br/>
 *
 * @author virjar
 * @since 0.2.2
 */
public class UserManager2ResourceLoader implements ResourceLoader {
    private UserResourceFacade userResourceFacade;

    public UserManager2ResourceLoader(UserResourceFacade userResourceFacade) {
        this.userResourceFacade = userResourceFacade;
    }

    @Override
    public boolean loadResource(Collection<ResourceItem> resourceItems) {
        for (User user : userResourceFacade.importUser()) {
            ResourceItem resourceItem = new ResourceItem();
            resourceItem.setData(JSONObject.toJSONString(user));
            resourceItem.setKey(user.getUserID());
            resourceItems.add(resourceItem);
        }
        return false;
    }


}
