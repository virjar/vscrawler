package com.virjar.vscrawler.core.net.user;

import com.alibaba.fastjson.JSONObject;
import com.virjar.vscrawler.core.VSCrawlerContext;
import com.virjar.vscrawler.core.resourcemanager.ResourceManager;
import com.virjar.vscrawler.core.resourcemanager.model.ResourceItem;
import lombok.Getter;

/**
 * Created by virjar on 2018/1/8.
 * <br/>
 * 新版本的用户管理器,他将不考虑用户数据存储,用来实现更加通用的用户信息分发以及封禁接近、甚至方便实现分布式环境下处理资源
 *
 * @author virjar
 * @since 0.2.2
 */
public class UserManager2 implements IUserManager {
    @Getter
    private VSCrawlerContext vsCrawlerContext;

    private ResourceManager resourceManager;

    public UserManager2(ResourceManager resourceManager, VSCrawlerContext vsCrawlerContext) {
        this.vsCrawlerContext = vsCrawlerContext;
        this.resourceManager = resourceManager;
        vsCrawlerContext.getAutoEventRegistry().registerObserver(this);
    }

    /**
     * recycle user resource to user resources pool, must make user instance detach from session
     *
     * @param user user instance
     */
    @Override
    public void returnUser(User user) {
        UserStatus userStatus = user.getUserStatus();
        if (userStatus == UserStatus.PASSWORDERROR || userStatus == UserStatus.NOTEXIST) {
            resourceManager.forbidden(vsCrawlerContext.makeUserResourceTag(), user.getUserID());
        } else {
            resourceManager.feedBack(vsCrawlerContext.makeUserResourceTag(), user.getUserID(), userStatus == UserStatus.OK);
        }

    }

    @Override
    public User allocateUser() {
        ResourceItem resourceItem = resourceManager.allocate(vsCrawlerContext.makeUserResourceTag());
        if (resourceItem == null) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(resourceItem.getData());
        return JSONObject.toJavaObject(jsonObject, User.class);
    }
}
