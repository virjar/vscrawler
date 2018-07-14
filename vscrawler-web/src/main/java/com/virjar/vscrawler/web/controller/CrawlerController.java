package com.virjar.vscrawler.web.controller;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.VSCrawler;
import com.virjar.vscrawler.web.model.CrawlerBean;
import com.virjar.vscrawler.web.model.CrawlerVO;
import com.virjar.vscrawler.web.model.WebJsonResponse;
import com.virjar.vscrawler.web.service.VSCrawlerManager;
import com.virjar.vscrawler.web.util.ReturnUtil;
import com.virjar.vscrawler.web.util.StackTraceTransformer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by virjar on 2018/2/3.<br>
 * 爬虫操作接口
 */
@RestController
@Slf4j
@RequestMapping("/crawler")
public class CrawlerController {
    @Resource
    private VSCrawlerManager crawlerManager;

    @RequestMapping("/uploadJar")
    @ResponseBody
    public WebJsonResponse<String> reloadJar(
            @RequestParam("file") MultipartFile file) {
        try {
            crawlerManager.reloadJar(file);
            return ReturnUtil.success("success");
        } catch (Exception e) {
            log.error("error when upload jar file", e);
            return ReturnUtil.failed(StackTraceTransformer.getStackTrack(e));
        }
    }

    @RequestMapping("/startCrawler")
    @ResponseBody
    public WebJsonResponse<String> start(@RequestParam("crawlerName") String crawlerName) {
        VSCrawler vsCrawler = crawlerManager.get(crawlerName);
        if (vsCrawler == null) {
            return ReturnUtil.failed("not crawler defined");
        }
        vsCrawler.start();
        return ReturnUtil.success("success");
    }

    @RequestMapping("/stopCrawler")
    @ResponseBody
    public WebJsonResponse<String> stop(@RequestParam("crawlerName") String crawlerName) {
        VSCrawler vsCrawler = crawlerManager.get(crawlerName);
        if (vsCrawler == null) {
            return ReturnUtil.failed("not crawler defined");
        }
        vsCrawler.stopCrawler();
        return ReturnUtil.success("success");
    }

    @RequestMapping("/crawlerStatus")
    @ResponseBody
    public WebJsonResponse<? extends List<CrawlerVO>> status() {
        return ReturnUtil.success(Lists.newArrayList(Collections2.transform(crawlerManager.getAllCrawler(), new Function<CrawlerBean, CrawlerVO>() {
            @Override
            public CrawlerVO apply(CrawlerBean input) {
                CrawlerVO crawlerVO = new CrawlerVO();
                crawlerVO.setCrawlerName(input.crawlerName());
                if (input.isReloadable()) {
                    crawlerVO.setJarPath(input.getVsCrawlerClassLoader().getJarFile().getAbsolutePath());
                    crawlerVO.setReloadAble(true);
                }
                VSCrawler crawler = input.getCrawler();
                crawlerVO.setStatus(crawler.status());
                crawlerVO.setActiveThreadNumber(crawler.activeWorker());
                crawlerVO.setActiveSessionNumber(crawler.getCrawlerSessionPool().sessionNumber());
                crawlerVO.setTotalSeed(crawler.getBerkeleyDBSeedManager().totalSeed());
                crawlerVO.setFinishedSeed(crawler.getBerkeleyDBSeedManager().finishedSeed());
                return crawlerVO;
            }
        })));
    }
}
