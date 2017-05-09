package com.virjar.vscrawler.net.proxy;

import java.util.List;

import org.apache.http.Header;

/**
 * Created by virjar on 17/5/9.<br/>
 * 代理的抽象,代理池的代理需要实现这个接口
 */
public interface Proxy {
    /**
     * 获得IP
     * 
     * @return
     */
    String getIp();

    /**
     * 代理端口
     * 
     * @return 端口
     */
    Integer getPort();

    /**
     * 代理账户,如果有
     * 
     * @return 账户
     */

    String getUsername();

    /**
     * 代理密码,如果有
     * 
     * @return 密码
     */
    String getPassword();

    /**
     * 头部认证方案,如果有
     * 
     * @return httpclient的header队列列表
     */
    List<Header> getAuthenticationHeaders();

    /**
     * 调用次方法应该触发ip下线,如果你实现了他
     */
    void offline();

    /**
     * 调用此方法应该封禁IP指定毫秒数,如果你实现了他
     * 
     * @param blockTimeStamp 时间戳,毫秒值
     */
    void block(long blockTimeStamp);

    /**
     * 每当vscrawler决定使用这个代理的时候,就会调用一次这个接口
     */
    void recordUsage();

    /**
     * 如果vscrawler发现本次代理使用失败,就会触发一次这方法的调用。代理本身应该需要对这个降权
     */
    void recordFailed();
}
