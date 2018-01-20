package com.virjar.vscrawler.samples;

import com.virjar.vscrawler.core.VSCrawler;
import com.virjar.vscrawler.core.VSCrawlerBuilder;
import com.virjar.vscrawler.core.net.session.CrawlerSession;
import com.virjar.vscrawler.core.processor.GrabResult;
import com.virjar.vscrawler.core.processor.SeedProcessor;
import com.virjar.vscrawler.core.seed.Seed;
import com.virjar.vscrawler.core.seed.SegmentResolver;
import org.joda.time.DateTime;

/**
 * Created by virjar on 17/6/17.<br/>
 * 这个是增量爬虫的测试,每个任务都会重复抓取,时间间隔为1分钟
 */
public class FutureCrawler {
    public static void main(String[] args) {
        VSCrawler vsCrawler = VSCrawlerBuilder.create().setStopWhileTaskEmptyDuration(2000)
                .setSegmentResolver(new SegmentResolver() {
                    @Override
                    public long resolveSegmentKey(long activeTime) {
                        // 按分钟分段,每隔一分钟重新抓取链接,这里只是为了测试,实际上不能设置这么短,建议按天分段
                        return new DateTime(activeTime).withSecondOfMinute(0).getMillis();
                    }
                }).setProcessor(new SeedProcessor() {
                    @Override
                    public void process(Seed seed, CrawlerSession crawlerSession, GrabResult crawlResult) {
                        // 建立一个种子副本
                        Seed copy = seed.copy();
                        // 设置生效时间为两分钟后
                        copy.setActiveTimeStamp(DateTime.now().plusMinutes(1).getMillis());
                        // 返回新种子
                        crawlResult.addSeed(copy);
                    }
                }).build();

        // 当前所有demo都会清空task,否则不同爬虫的数据可能紊乱
        vsCrawler.clearTask();
        vsCrawler.pushSeed("https://www.meitulu.com/item/6892.htm");
        vsCrawler.start();
    }
}
