package com.virjar.vscrawler.core.net.proxy.strategy;

import com.virjar.vscrawler.core.net.proxy.IPPool;
import com.virjar.vscrawler.core.net.proxy.Proxy;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;

import com.virjar.vscrawler.core.net.session.CrawlerSession;

/**
 * Created by virjar on 17/5/13.
 * 
 * @since 0.0.1
 * @author virjar
 */
public interface ProxyPlanner {
    /**
     * 自定义代理规则,如果觉得默认的三种策略(每次请求换代理,每个session换代理,每个用户切换代理)不够使用,可以自定义代理策略
     * 
     * @param host 请求目标主机
     * @param request 请求体
     * @param context 请求的context
     * @param ipPool 代理池对象,可以获取一个IP
     * @param crawlerSession vsCrawler的session,里面封装了单个用户一次回话的所有信息
     * @return 代理对象
     */
    Proxy determineProxy(HttpHost host, HttpRequest request, HttpContext context, IPPool ipPool,
                         CrawlerSession crawlerSession);
}
