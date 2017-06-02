package com.virjar.vscrawler.over.webmagic7;

import java.net.URL;

import com.virjar.vscrawler.core.net.session.CrawlerSession;
import com.virjar.vscrawler.core.seed.Seed;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * Created by virjar on 17/5/20.
 */
public class WebMagicDownloaderDelegator extends WebMagicProcessorDelegator {
    private Downloader downloader;

    public WebMagicDownloaderDelegator(PageProcessor pageProcessor, Downloader downloader) {
        super(pageProcessor);
        this.downloader = downloader;
    }

    @Override
    protected String download(CrawlerSession crawlerSession, URL url) {
        Page page = downloader.download(CovertUtil.convertSeed(new Seed(url.toString())), CovertUtil.task);
        if (page == null) {
            return null;
        }
        return page.getRawText();
    }
}
