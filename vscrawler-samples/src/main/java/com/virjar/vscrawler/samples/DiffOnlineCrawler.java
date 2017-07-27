package com.virjar.vscrawler.samples;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.virjar.sipsoup.parse.XpathParser;
import com.virjar.vscrawler.core.VSCrawler;
import com.virjar.vscrawler.core.VSCrawlerBuilder;
import com.virjar.vscrawler.core.processor.AutoParseSeedProcessor;
import com.virjar.vscrawler.core.processor.CrawlResult;
import com.virjar.vscrawler.core.seed.HtmlPageSeedKeyResolver;
import com.virjar.vscrawler.core.seed.Seed;
import com.virjar.vscrawler.core.selector.xpath.Xpath;
import com.virjar.vscrawler.core.util.PathResolver;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by virjar on 17/7/27.
 * 抓取 http://www.diff-online.com/ 国外太慢了,抓回来自己改一改
 */
public class DiffOnlineCrawler {
    public static void main(String[] args) {
        // 启动爬虫
        VSCrawler vsCrawler = VSCrawlerBuilder.create()// 创建一个构造器
                .setSeedKeyResolver(new HtmlPageSeedKeyResolver())//使用网页消重器,处理锚点重复问题
                .setProcessor(new AutoParseSeedProcessor() {
                    private List<String> allUrl(Document document) {
                        List<String> strings = XpathParser.compileNoError("/css('a')::absUrl('href')").evaluateToString(document);
                        strings.addAll(XpathParser.compileNoError("/css('script')::absUrl('src')").evaluateToString(document));
                        strings.addAll(XpathParser.compileNoError("/css('link')::absUrl('href')").evaluateToString(document));
                        strings.addAll(XpathParser.compileNoError("/css('img')::absUrl('src')").evaluateToString(document));
                        return strings;
                    }

                    @Override
                    protected void parse(Seed seed, String result, CrawlResult crawlResult) {
                        if (StringUtils.isEmpty(result)) {
                            return;
                        }
                        try {
                            Files.write(result, new File(PathResolver.commonDownloadPath("~/Desktop/", seed.getData())), Charsets.UTF_8);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            List<String> strings = allUrl(Jsoup.parse(result, seed.getData()));
                            crawlResult.addStrSeeds(Lists.newLinkedList(Iterables.filter(strings, new Predicate<String>() {
                                @Override
                                public boolean apply(String input) {
                                    return StringUtils.contains(input, "www.diff-online.com");
                                }
                            })));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                })
                .setWorkerThreadNumber(20).build();
        vsCrawler.clearTask();

        vsCrawler.start();

        // System.out.println("休眠10s,观察爬虫阻塞等待逻辑是否正确");
        // 休眠10s
        // CommonUtil.sleep(10000);

        // 增加种子
        System.out.println("注入一个种子任务");
        vsCrawler.pushSeed("http://www.diff-online.com/");
    }
}
