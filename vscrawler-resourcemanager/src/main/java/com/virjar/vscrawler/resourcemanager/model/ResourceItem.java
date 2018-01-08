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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResourceItem)) return false;

        ResourceItem that = (ResourceItem) o;

        return key.equals(that.key);

    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
