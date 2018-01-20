package com.virjar.vscrawler.samples.pipline.javabean;

import com.alibaba.fastjson.JSONObject;
import com.virjar.vscrawler.core.VSCrawlerBuilder;
import com.virjar.vscrawler.core.net.session.CrawlerSession;
import com.virjar.vscrawler.core.processor.CrawlResult;
import com.virjar.vscrawler.core.processor.SeedProcessor;
import com.virjar.vscrawler.core.seed.Seed;

import java.util.Collection;

/**
 * Created by virjar on 2018/1/20.
 */
public class JavaBeanProcessor implements SeedProcessor {
    @Override
    public void process(Seed seed, CrawlerSession crawlerSession, CrawlResult crawlResult) {

        //this is java bean
        TestModel testModel = new TestModel();
        crawlResult.addResult(JSONObject.toJSONString(testModel));
    }

    public static void main(String[] args) {
        VSCrawlerBuilder.create().setProcessor(new JavaBeanProcessor()).addPipeline(new JavaBeanPipline<TestModel>(TestModel.class) {
            @Override
            void saveBean(Collection<TestModel> beans, Seed seed) {
                System.out.println("save bean for beans:" + beans);
            }
        });
    }
}
