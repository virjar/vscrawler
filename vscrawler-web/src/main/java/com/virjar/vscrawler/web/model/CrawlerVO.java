package com.virjar.vscrawler.web.model;

import lombok.Data;

/**
 * Created by virjar on 2018/2/3.<br>
 * crawler 的 view object,用于和前端交互
 */
@Data
public class CrawlerVO {
    private String crawlerName;
    private String jarPath;
    private boolean reloadAble;
    private String status;
    private Integer activeThreadNumber;
    private Integer activeSessionNumber;
    private Long totalSeed;
    private Long finishedSeed;
}
