package com.virjar.vscrawler.samples;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.virjar.dungproxy.client.httpclient.HeaderBuilder;
import com.virjar.sipsoup.parse.XpathParser;
import com.virjar.vscrawler.core.VSCrawler;
import com.virjar.vscrawler.core.VSCrawlerBuilder;
import com.virjar.vscrawler.core.net.session.CrawlerSession;
import com.virjar.vscrawler.core.processor.GrabResult;
import com.virjar.vscrawler.core.processor.SeedProcessor;
import com.virjar.vscrawler.core.seed.Seed;
import com.virjar.vscrawler.core.util.PathResolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;

/**
 * Created by virjar on 17/6/11.<br/>
 * 此乃美女爬虫
 */
public class BeautyCrawler {

    public static void main(String[] args) throws IOException {

        VSCrawler vsCrawler = VSCrawlerBuilder.create()
                .setCrawlerName("beautyCrawler")
                .setProcessor(new SeedProcessor() {

                    private void handlePic(Seed seed, CrawlerSession crawlerSession) {
                        Header[] headers = HeaderBuilder.create().withRefer(seed.getExt().get("refer")).defaultCommonHeader().buildArray();
                        byte[] entity = crawlerSession.getCrawlerHttpClient().getEntity(seed.getData(), headers);
                        if (entity == null) {
                            seed.retry();
                            return;
                        }
                        try {
                            Files.write(entity, // 文件根据网站,路径,base自动计算
                                    new File(PathResolver.sourceToUnderLine("~/Desktop/testpic", seed.getData())));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void process(final Seed seed, CrawlerSession crawlerSession, GrabResult crawlResult) {
                        if (StringUtils.endsWithIgnoreCase(seed.getData(), ".jpg")) {
                            handlePic(seed, crawlerSession);
                        } else {
                            String s = crawlerSession.getCrawlerHttpClient().get(seed.getData());
                            if (s == null) {
                                seed.retry();
                                return;
                            }
                            // 将下一页的链接和图片链接抽取出来
                            crawlResult.addSeeds(Lists.newArrayList(Iterables.transform(XpathParser
                                    .compileNoError(
                                            "/css('#pages a')::self()[contains(text(),'下一页')]/absUrl('href') | /css('.content')::center/img/@src")
                                    .evaluateToString(Jsoup.parse(s, seed.getData())), new Function<String, Seed>() {
                                @Override
                                public Seed apply(String input) {
                                    Seed ret = new Seed(input);
                                    if (StringUtils.endsWith(input, ".jpg")) {
                                        ret.getExt().put("refer", seed.getData());
                                    }
                                    return ret;
                                }
                            })));
                        }
                    }

                }).setWorkerThreadNumber(15).setSessionPoolCoreSize(20).setSessionPoolMaxSize(25).build();

        // 清空历史爬去数据,或者会断点续爬
        vsCrawler.clearTask();

        vsCrawler.pushSeed("https://www.meitulu.com/item/2125.html");
        vsCrawler.pushSeed("https://www.meitulu.com/item/6892.html");
        vsCrawler.pushSeed("https://www.meitulu.com/item/2124.html");
        vsCrawler.pushSeed("https://www.meitulu.com/item/2120.html");
        vsCrawler.pushSeed("https://www.meitulu.com/item/2086.html");

        // 开始爬虫
        vsCrawler.start();
    }
}
