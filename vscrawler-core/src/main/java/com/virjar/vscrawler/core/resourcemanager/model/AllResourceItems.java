package com.virjar.vscrawler.core.resourcemanager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by virjar on 2018/1/11.<br>
 * 所有的资源打包,包括轮询队列,leave队列,forbidden队列
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllResourceItems {
    private List<ResourceItem> pollingQueue;
    private List<ResourceItem> leaveQueue;
    private List<ResourceItem> forbiddenQueue;
}
