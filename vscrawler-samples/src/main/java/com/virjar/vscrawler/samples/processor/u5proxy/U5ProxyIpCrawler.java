package com.virjar.vscrawler.samples.processor.u5proxy;

import com.virjar.vscrawler.core.VSCrawlerBuilder;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.AbstractAutoProcessModel;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.AnnotationProcessorBuilder;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.annotation.AutoProcessor;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.annotation.FetchChain;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.annotation.Xpath;
import com.virjar.vscrawler.core.seed.Seed;
import lombok.Getter;

import java.util.List;

/**
 * Created by virjar on 2017/12/16.<br/>
 * 抓取无忧代理的代理ip,本demo展示注解,多行抽取功能
 */
@AutoProcessor(seedPattern = "http://www\\.data5u\\.com/free/index\\.shtml")
@FetchChain("$css{.l2}")
//对model本身的抽取,必须使用FetchChain注解,FetchChain是链式抽取标记,可以支持$css{} $xpath{} $regex{} $jsonpath{} $stringrule{}
//多个规则使用直接按顺序书写即可 如: $css{.class}$xpath{//div[@data='abc']//a/absUrl()}$stringrule{deleteWhiteSpace(self())}
//如果表达式中存在关键字"{"或者"}",使用反斜线转义即可
public class U5ProxyIpCrawler extends AbstractAutoProcessModel {

    @Xpath("/span[1]/li/text()")
    @Getter
    private String ip;

    @Xpath("//css('.port')::self()/text()")
    @Getter
    //type可以是一般的常用类型,框架会自动做类型转换,转换规则满足一般的弱语言规则,当不能强转时将会报错,如果你抓取的网站格式可能超过你预期范围,建议使用string来接收
    private Integer port;

    @Xpath("/span[3]/li/allText()")
    //关于allText和text的区别,allText将会抽取子节点文本,,请了解SipSoup这个项目
    @Getter
    private String anonymous;

    @Xpath("/span[4]/li/allText()")
    @Getter
    private String type;

    @Xpath("/span[5]/li/allText()")
    @Getter
    private String country;

    @Xpath("/span[6]/li/allText()")
    @Getter
    private String city;

    @Xpath("/span[7]/li/allText()")
    @Getter
    private String isp;

    @Xpath("/span[8]/li/text()")
    @Getter
    private String speed;

    @Xpath("/span[9]/li/text()")
    @Getter
    private String lastValidateTime;

    @Override
    protected boolean hasGrabSuccess() {
        //这里可以决定当前抓取是否成功
        return super.hasGrabSuccess();
    }

    @Override
    protected void afterAutoFetch() {
        //这里可以在自动抽取逻辑后修复数据
        super.afterAutoFetch();
    }

    @Override
    protected void beforeAutoFetch() {
        //可以可以在自动抽取前准备数据
        super.beforeAutoFetch();
    }

    @Override
    protected List<Seed> newSeeds() {
        //如果注解不能准确的表示新种子,可以在这里通过代码来产生
        return super.newSeeds();
    }

    public static void main(String[] args) {
        VSCrawlerBuilder
                .create()
                .setProcessor(AnnotationProcessorBuilder.create().registryBean(U5ProxyIpCrawler.class).build())
                .setCrawlerName("u5ProxyCrawler")
                .build()
                .clearTask()
                .pushSeed("http://www.data5u.com/free/index.shtml")
                .run();
    }
}
