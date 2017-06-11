package com.virjar.vscrawler.samples.xpath;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.common.io.Files;
import com.virjar.dungproxy.client.util.CommonUtil;
import com.virjar.vscrawler.core.VSCrawler;
import com.virjar.vscrawler.core.VSCrawlerBuilder;
import com.virjar.vscrawler.core.event.support.AutoEventRegistry;
import com.virjar.vscrawler.core.event.systemevent.SeedEmptyEvent;
import com.virjar.vscrawler.core.net.session.CrawlerSession;
import com.virjar.vscrawler.core.processor.CrawlResult;
import com.virjar.vscrawler.core.processor.SeedProcessor;
import com.virjar.vscrawler.core.seed.Seed;
import com.virjar.vscrawler.core.selector.xpath.core.parse.XpathParser;
import com.virjar.vscrawler.core.selector.xpath.model.XpathEvaluator;
import com.virjar.vscrawler.core.util.PathResolver;
import com.virjar.vscrawler.samples.EmptyPipeline;

/**
 * Created by virjar on 17/6/11.
 */
public class XpathSelectTest {
    public static void main(String[] args) throws IOException {
        InputStream stream = XpathSelectTest.class.getResourceAsStream("/道重沙由美.html");
        String html = IOUtils.toString(stream);
        IOUtils.closeQuietly(stream);

        Document document = Jsoup.parse(html);
        XpathEvaluator xpathEvaluator = XpathParser.compileNoError("//css('.ad-thumb-list .inner')::*//a/@href");
        List<String> strings = xpathEvaluator.evaluateToString(document);

        VSCrawler vsCrawler = VSCrawlerBuilder.create().addPipeline(new EmptyPipeline())
                .setProcessor(new SeedProcessor() {
                    @Override
                    public void process(Seed seed, CrawlerSession crawlerSession, CrawlResult crawlResult) {
                        byte[] entity = crawlerSession.getCrawlerHttpClient().getEntity(seed.getData());
                        if (entity == null) {
                            seed.retry();
                            return;
                        }
                        try {
                            Files.write(entity, // 文件根据网站,路径,base自动计算
                                    new File(PathResolver.resourceName("~/Desktop/testpic", seed.getData())));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).build();

        // 清空历史爬去数据,或者会断点续爬
        vsCrawler.clearTask();

        // 加入种子
        for (String str : strings) {
            vsCrawler.pushSeed(str);
        }

        vsCrawler.addCrawlerStartCallBack(new VSCrawler.CrawlerStartCallBack() {
            @Override
            public void onCrawlerStart(final VSCrawler vsCrawler) {
                AutoEventRegistry.getInstance().registerObserver(new SeedEmptyEvent() {
                    @Override
                    public void onSeedEmpty() {// 如果收到任务为空消息的话,尝试停止爬虫
                        new Thread() {
                            @Override
                            public void run() {
                                CommonUtil.sleep(10000);
                                if (vsCrawler.activeWorker() == 0) {
                                    vsCrawler.stopCrawler();
                                }
                            }
                        }.start();
                    }
                });
            }
        });

        // 开始爬虫
        vsCrawler.start();
    }
}
