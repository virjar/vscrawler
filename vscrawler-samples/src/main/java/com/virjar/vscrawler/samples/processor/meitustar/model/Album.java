package com.virjar.vscrawler.samples.processor.meitustar.model;

import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.AbstractAutoProcessModel;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.annotation.NewSeed;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.annotation.Xpath;
import lombok.Getter;

import java.util.List;

/**
 * Created by virjar on 2017/12/16.
 * <br/>
 * 写真集
 */
public class Album extends AbstractAutoProcessModel {
    /**
     * 写真集封面
     */
    @Getter
    @Xpath("/a/@href")
    private String coverImage;

    /**
     * 写真集图片数量
     */
    @Getter
    @Xpath("/p[1]/text()")
    private String imageSize;

    /**
     * 写真集发行机构
     */
    @Getter
    @Xpath("/p[2]/a/text()")
    private List<String> jigou;

    /**
     * 写真集模特(英语不好,就不翻译了)
     */
    @Getter
    @Xpath("/p[3]/a/text()")
    private List<String> mote;

    /**
     * 对应标签
     */
    @Getter
    @Xpath("/p[4]/a/text()")
    private List<String> tag;

    @Getter
    //@NewSeed //如果需要抓取详细信息,那么应该开启这个注解,并实现详细页面的抽取逻辑
    @Xpath("/p[@class='p_title']/a/@href")
    private String detailUrl;

}
