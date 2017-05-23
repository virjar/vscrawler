package com.virjar.vscrawler.core.support.webmagic;

import com.virjar.vscrawler.core.processor.SeedProcessor;
import com.virjar.vscrawler.core.serialize.Pipeline;

import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * Created by virjar on 17/5/20.
 */
public class WebMagicBridage {
    public static SeedProcessor transformProcessor(PageProcessor pageProcessor) {
        return new WebMagicProcessorDelegator(pageProcessor);
    }

    public static SeedProcessor transformProcessorWithDownloader(PageProcessor pageProcessor, Downloader downloader) {
        return new WebMagicDownloaderDelegator(pageProcessor, downloader);
    }

    public static Pipeline transfromPipeline(us.codecraft.webmagic.pipeline.Pipeline pipeline) {
        return new WebMagicPipelineDelegator(pipeline);
    }
}
