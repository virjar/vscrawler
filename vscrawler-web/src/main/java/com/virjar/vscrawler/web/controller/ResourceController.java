package com.virjar.vscrawler.web.controller;

import com.virjar.vscrawler.core.VSCrawler;
import com.virjar.vscrawler.core.VSCrawlerContext;
import com.virjar.vscrawler.core.resourcemanager.model.AllResourceItems;
import com.virjar.vscrawler.web.model.WebJsonResponse;
import com.virjar.vscrawler.web.service.VSCrawlerManager;
import com.virjar.vscrawler.web.util.ReturnUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by virjar on 2018/2/3.<br>
 * 资源操作接口
 */
@RestController
@Slf4j
@RequestMapping("/resource")
public class ResourceController {
    @Resource
    private VSCrawlerManager crawlerManager;

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
