package com.virjar.vscrawler.web.sample;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.virjar.vscrawler.core.net.session.CrawlerSession;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.AbstractAutoProcessModel;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.annotation.*;
import com.virjar.vscrawler.core.seed.Seed;
import lombok.Data;

import java.util.List;

/**
 * Created by virjar on 2018/2/3.<br>
 * 开源中国项目列表
 */
@AutoProcessor(seedPattern = ".*")
@FetchChain("$css{#git-discover-list .item}")
@Data
public class GitEEProject extends AbstractAutoProcessModel {
    /**
     * 头像
     */
    @Xpath("//css('.avatar'::self()/a/absUrl('src')")
    private String avatar;

    /**
     * 作者id,一般为英文名称,这里示例如何使用链式抽取器,使用css定位到a标签,使用xpath抽取属性值(这是因为Jsoup自带的css选择器只能实现节点级别的定位,无法抽取字符串,如属性值),使用字符串函数删除第一个字符(斜杠)
     */
    @FetchChain("$css{.avatar} $xpath{/@href} $stringrule{substring(self(),1)}")
    private String authorId;

    @Xpath("//css('.project-title .project-namespace-path')::self()/absUrl('href')")
    private String projectUrl;

    @Xpath("//css('.project-title .project-namespace-path')::self()/text()")
    private String authorNickName;

    /**
     * 是否是马云推荐项目,这里来感受一下框架的类型自动转换功能
     */
    @FetchChain("$css{.icon-recommended} $xpath{/@title} $stringrule{startsWith(self(),'码云推荐项目')}")
    private boolean isRecommend;

    /**
     * 码云所谓最有价值的项目
     */
    @FetchChain("$css{.gvp-label}")
    private boolean GVP;
    /**
     * 关注人数
     */
    @Xpath("//css('.watch-star-fork')::self()/a[contains(@href,'watchers')]/span/text()")
    private String watchers;

    /**
     * star 人数
     */
    @Xpath("//css('.watch-star-fork')::self()/a[contains(@href,'stargazer')]/span/text()")
    private String stargazer;

    /**
     * fork 人数
     */
    @Xpath("//css('.watch-star-fork')::self()/a[contains(@href,'members')]/span/text()")
    private String forks;

    /**
     * 项目描述
     */
    @CSS(".project-desc")
    private String projectDesc;

    /**
     * 标签,这里感受一下框架的集合注入能力
     */
    @CSS(value = ".project-meta a", elementType = String.class)
    private List<String> meta;

    /**
     * 最后更新时间
     */
    @CSS(".timeago")
    private String timeago;

    /**
     * 默认下载策略,是将种子当中url,但是我们现在实际传入的是一个项目类型tag,不是url,所以需要特殊处理
     */
    @DownLoadMethod
    public String download(Seed seed, CrawlerSession crawlerSession) {
        String param;
        try {
            JSONObject jsonObject = JSONObject.parseObject(seed.getData());
            param = jsonObject.getString("param");
        } catch (JSONException e) {
            param = seed.getData();
        }
        String url = "https://gitee.com/explore/starred/" + param;
        String html = crawlerSession.getCrawlerHttpClient().get(url);
        setBaseUrl(url);
        return html;
    }
}
