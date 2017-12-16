package com.virjar.vscrawler.samples.processor.meitustar.model;

import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.AbstractAutoProcessModel;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.annotation.AutoProcessor;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.annotation.NewSeed;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.annotation.Xpath;

import java.util.List;

/**
 * Created by virjar on 2017/12/16.
 */
@AutoProcessor(seedPattern = ".*", priority = -100)
public class AllUrlExtractor extends AbstractAutoProcessModel {
    @NewSeed
    @Xpath("//a/absUrl('href')")
    //请注意,多行数据必须使用容器来注入
    private List<String> seedUrl;
}
