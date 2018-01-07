package com.virjar.vscrawler.resourcemanager.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by virjar on 2018/1/4.
 */
@Getter
@Setter
public class ResourceItem {
    private String tag;
    private String key;
    private String data;
    private double score;
    private int status;
    private long validTimeStamp;
}
