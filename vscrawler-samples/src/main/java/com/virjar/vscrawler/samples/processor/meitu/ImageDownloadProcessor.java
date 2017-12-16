package com.virjar.vscrawler.samples.processor.meitu;

import com.google.common.io.Files;
import com.virjar.dungproxy.client.httpclient.HeaderBuilder;
import com.virjar.vscrawler.core.net.session.CrawlerSession;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.AbstractAutoProcessModel;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.annotation.AutoProcessor;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.annotation.DownLoadMethod;
import com.virjar.vscrawler.core.seed.Seed;
import com.virjar.vscrawler.core.util.PathResolver;
import org.apache.http.Header;

import java.io.File;
import java.io.IOException;

/**
 * Created by virjar on 2017/12/16.<br/>
 * 用来下载图片,提取出来的目的是让图片种子也作为单独的任务,享受重试机制带来的抓取完整性保证
 */
@AutoProcessor(seedPattern = ".*\\.jpg")
public class ImageDownloadProcessor extends AbstractAutoProcessModel {

    @DownLoadMethod//DownLoadMethod修饰方法,当下载逻辑不是get,或者需要设置下载参数等特殊逻辑的时候,可以通过这个注解替换默认下载逻辑
    //不过由DownLoadMethod修饰的方法,比如满足DownLoadMethod需要的方法签名格式(返回类型为string,有且仅包含seed,crawlerSession两个参数)
    public String downLoadImage(Seed seed, CrawlerSession crawlerSession) {
        Header[] headers = HeaderBuilder.create().withRefer(seed.getExt().get("fromUrl")).defaultCommonHeader().buildArray();
        byte[] entity = crawlerSession.getCrawlerHttpClient().getEntity(seed.getData(), headers);
        if (entity == null) {
            seed.retry();
            return null;
        }
        try {
            Files.write(entity, // 文件根据网站,路径,base自动计算
                    new File(PathResolver.sourceToUnderLine("~/Desktop/testpic", seed.getData())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
