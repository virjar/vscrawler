package com.virjar.vscrawler.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.virjar.vscrawler.core.VSCrawler;
import com.virjar.vscrawler.core.processor.GrabResult;
import com.virjar.vscrawler.core.seed.Seed;
import com.virjar.vscrawler.web.model.GrabRequest;
import com.virjar.vscrawler.web.model.WebJsonResponse;
import com.virjar.vscrawler.web.service.VSCrawlerManager;
import com.virjar.vscrawler.web.util.ReturnUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by virjar on 2018/1/17.<br>
 * 在线抓取接口
 */
@RestController
@Slf4j
public class GrabController {
    @Resource
    private VSCrawlerManager crawlerManager;

    @RequestMapping("/grab")
    @ResponseBody
    public WebJsonResponse<?> grab(@RequestBody GrabRequest grabRequestBean) {
        try {
            VSCrawler vsCrawler = crawlerManager.get(grabRequestBean.getCrawlerName());
            if (vsCrawler == null) {
                return ReturnUtil.failed("no crawler defined :" + grabRequestBean.getCrawlerName());
            }

            Seed seed = new Seed(JSONObject.toJSONString(grabRequestBean));
            GrabResult crawlResult = vsCrawler.grabSync(seed);
            List<Object> strings = crawlResult.allEntityResult();
            if (strings.size() == 1) {
                return ReturnUtil.success(strings.get(0));
            } else if (strings.size() == 0 && seed.getRetry() > 0) {
                return ReturnUtil.failed("timeOut", ReturnUtil.status_timeout);
            } else {
                return ReturnUtil.success(strings);
            }
        } catch (Exception e) {
            return ReturnUtil.failed(e.getMessage());
        }
    }
}
