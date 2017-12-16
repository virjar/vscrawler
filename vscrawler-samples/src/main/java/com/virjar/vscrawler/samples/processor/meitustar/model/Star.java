package com.virjar.vscrawler.samples.processor.meitustar.model;

import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.AbstractAutoProcessModel;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.annotation.AutoProcessor;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.annotation.NewSeed;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.annotation.Xpath;
import lombok.Getter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by virjar on 2017/12/16.
 */
@AutoProcessor(seedPattern = "https://www\\.meitulu\\.com.*")
public class Star extends AbstractAutoProcessModel {

    @Getter
    private String starName;

    @Getter
    //@Xpath("/css('.listtags_r')::self()/allText()") //如果只抽文本,则使用allText
    @Xpath("/css('.listtags_r')::self()/html()")
    private String desciption;

    @Xpath("/css('.listtags_l')::img/absUrl('src')")
    //absUrl可以处理相对路径和绝对路径问题,如果url是相对路径,absUrl会自动转化为绝对路径
    @Getter
    private String headPicture;

    @Xpath(value = "//div[@class='boxs']/ul[@class='img']/li", elementType = Album.class)
    //由于泛型擦除可能,框架不能自动判断list内部的Album类型,所以需要elementType来制定,否则这里抽取得到的原始数据类型是element(Jsoup对象)
    @Getter
    private List<Album> albumList;

    @NewSeed
    @Xpath("//a/absUrl('href')")
    //@JSONField(serialize = false)
    private List<String> seedUrl;

    @Override
    protected void beforeAutoFetch() {
        super.beforeAutoFetch();
        Matcher matcher = Pattern.compile("https://www\\.meitulu\\.com/t/([^/]+)").matcher(baseUrl);
        if (matcher.find()) {
            starName = matcher.group(1);
        }
    }

    public static void main(String[] args) {

    }
}
