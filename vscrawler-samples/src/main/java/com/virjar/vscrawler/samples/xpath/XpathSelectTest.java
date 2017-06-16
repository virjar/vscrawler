package com.virjar.vscrawler.samples.xpath;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

import com.google.common.io.Files;
import com.virjar.dungproxy.client.util.CommonUtil;
import com.virjar.sipsoup.function.FunctionEnv;
import com.virjar.sipsoup.parse.XpathParser;
import com.virjar.vscrawler.core.VSCrawler;
import com.virjar.vscrawler.core.VSCrawlerBuilder;
import com.virjar.vscrawler.core.event.support.AutoEventRegistry;
import com.virjar.vscrawler.core.event.systemevent.SeedEmptyEvent;
import com.virjar.vscrawler.core.net.session.CrawlerSession;
import com.virjar.vscrawler.core.processor.CrawlResult;
import com.virjar.vscrawler.core.processor.SeedProcessor;
import com.virjar.vscrawler.core.seed.Seed;
import com.virjar.vscrawler.core.util.PathResolver;
import com.virjar.vscrawler.samples.EmptyPipeline;

/**
 * Created by virjar on 17/6/11.
 */
public class XpathSelectTest {
    static {
        FunctionEnv.registSelectFunction(new AbsUrlFunction());
        FunctionEnv.registFilterFunction(new MyContainsFunction());
        FunctionEnv.registSelectFunction(new MyTagSelectFunction());
    }

    public static void main(String[] args) throws IOException {

        VSCrawler vsCrawler = VSCrawlerBuilder.create().addPipeline(new EmptyPipeline())
                .setProcessor(new SeedProcessor() {

                    private void handlePic(Seed seed, CrawlerSession crawlerSession) {
                        byte[] entity = crawlerSession.getCrawlerHttpClient().getEntity(seed.getData());
                        if (entity == null) {
                            seed.retry();
                            return;
                        }
                        try {
                            Files.write(entity, // 文件根据网站,路径,base自动计算
                                    new File(PathResolver.onlySource("~/Desktop/testpic", seed.getData())));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void process(Seed seed, CrawlerSession crawlerSession, CrawlResult crawlResult) {
                        if (StringUtils.endsWithIgnoreCase(seed.getData(), ".jpg")) {
                            handlePic(seed, crawlerSession);
                        } else {
                            String s = crawlerSession.getCrawlerHttpClient().get(seed.getData());
                            if (s == null) {
                                seed.retry();
                                return;
                            }
                            // 将下一页的链接和图片链接抽取出来
                            crawlResult.addStrSeeds(XpathParser
                                    .compileNoError(
                                            "/css('#pages a')::self()[contains(text(),'下一页')]/absUrl('href') | /css('.content')::center/img/@src")
                                    .evaluateToString(Jsoup.parse(s, seed.getData())));
                        }
                    }

                }).build();

        // 清空历史爬去数据,或者会断点续爬
        vsCrawler.clearTask();

        vsCrawler.pushSeed("https://www.meitulu.com/item/2125.html");
        vsCrawler.pushSeed("https://www.meitulu.com/item/6892.html");
        vsCrawler.pushSeed("https://www.meitulu.com/item/2124.html");
        vsCrawler.pushSeed("https://www.meitulu.com/item/2120.html");
        vsCrawler.pushSeed("https://www.meitulu.com/item/2086.html");
        vsCrawler.pushSeed("https://www.meitulu.com/item/2066.html");

        vsCrawler.addCrawlerStartCallBack(new VSCrawler.CrawlerStartCallBack() {
            @Override
            public void onCrawlerStart(final VSCrawler vsCrawler) {
                AutoEventRegistry.getInstance().registerObserver(new SeedEmptyEvent() {
                    @Override
                    public void onSeedEmpty() {// 如果收到任务为空消息的话,尝试停止爬虫
                        new Thread() {
                            @Override
                            public void run() {
                                CommonUtil.sleep(10000);// 如果连续10s都没有新任务,则停止爬虫
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
