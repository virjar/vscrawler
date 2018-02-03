package com.virjar.vscrawler.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.virjar.vscrawler.core.VSCrawler;
import com.virjar.vscrawler.core.VSCrawlerContext;
import com.virjar.vscrawler.core.processor.GrabResult;
import com.virjar.vscrawler.core.resourcemanager.model.AllResourceItems;
import com.virjar.vscrawler.core.seed.Seed;
import com.virjar.vscrawler.web.model.GrabRequest;
import com.virjar.vscrawler.web.model.WebJsonResponse;
import com.virjar.vscrawler.web.service.VSCrawlerManager;
import com.virjar.vscrawler.web.util.ReturnUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by virjar on 2018/1/17.
 */
@RestController
@Slf4j
public class GrabController {
    @Resource
    private VSCrawlerManager crawlerManager;

    @RequestMapping("/grab")
    @ResponseBody
    public WebJsonResponse<String> grab(@RequestBody GrabRequest grabRequestBean) {
        long grabStartTimeStamp = System.currentTimeMillis();
        try {
            VSCrawler vsCrawler = crawlerManager.get(grabRequestBean.getAppSource());
            if (vsCrawler == null) {
                return ReturnUtil.failed("no crawler defined :" + grabRequestBean.getAppSource());
            }

            Seed seed = new Seed(JSONObject.toJSONString(grabRequestBean));
            GrabResult crawlResult = vsCrawler.grabSync(seed);
            List<String> strings = crawlResult.allResult();
            if (strings.size() == 1) {
                return ReturnUtil.success(strings.get(0));
            } else if (strings.size() == 0 && seed.getRetry() > 0) {
                return ReturnUtil.failed("timeOut", ReturnUtil.status_timeout);
            } else {
                return ReturnUtil.success(JSONObject.toJSONString(strings));
            }
        } catch (Exception e) {
            return ReturnUtil.failed(e.getMessage());
        }
    }

    @RequestMapping("/reloadAccount")
    @ResponseBody
    public WebJsonResponse<String> reloadAccount(@RequestParam("appSource") String appSource) {
        VSCrawler vsCrawler = crawlerManager.get(appSource);
        if (vsCrawler == null) {
            return ReturnUtil.failed("no crawler defined :" + appSource);
        }
        VSCrawlerContext vsCrawlerContext = vsCrawler.getVsCrawlerContext();
        vsCrawlerContext.getResourceManager().reloadResource(vsCrawlerContext.makeUserResourceTag());
        return ReturnUtil.success("success");
    }

    @RequestMapping("/reloadResource")
    @ResponseBody
    public WebJsonResponse<String> reloadResource(@RequestParam("appSource") String appSource,
                                                  @RequestParam("resourceName") String resourceName) {
        VSCrawler vsCrawler = crawlerManager.get(appSource);
        if (vsCrawler == null) {
            return ReturnUtil.failed("no crawler defined :" + appSource);
        }
        VSCrawlerContext vsCrawlerContext = vsCrawler.getVsCrawlerContext();
        vsCrawlerContext.getResourceManager().reloadResource(resourceName);
        return ReturnUtil.success("success");
    }

    @RequestMapping("/resourceStatus")
    @ResponseBody
    public WebJsonResponse<AllResourceItems> resourceStatus(@RequestParam("appSource") String appSource,
                                                            @RequestParam("resourceName") String resourceName) {
        VSCrawler vsCrawler = crawlerManager.get(appSource);
        if (vsCrawler == null) {
            return ReturnUtil.failed("no crawler defined :" + appSource);
        }
        return ReturnUtil.success(vsCrawler.getVsCrawlerContext().getResourceManager().queueStatus(resourceName));
    }
}
