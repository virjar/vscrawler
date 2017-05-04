package com.virjar.vscrawler;

import com.virjar.vscrawler.net.CrawlerHttpClientGenerator;
import com.virjar.vscrawler.net.session.LoginHandler;
import com.virjar.vscrawler.net.user.UserResourceFacade;

/**
 * Created by virjar on 17/4/30.<br/>
 * build a crawlerInstance
 *
 * @author virjar
 * @since 0.0.1
 */
public class VSCrawlerBuilder {
    /**
     * httpclient构造器,可能需要定制自己的httpclient
     */
    private CrawlerHttpClientGenerator crawlerHttpClientGenerator;

    /**
     * 登录处理器
     */
    private LoginHandler loginHandler;

    /**
     * 用户数据导入源
     */
    private UserResourceFacade userResourceFacade;

}
