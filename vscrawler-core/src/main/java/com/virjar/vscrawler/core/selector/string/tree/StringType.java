package com.virjar.vscrawler.core.selector.string.tree;

import lombok.Getter;

/**
 * Created by virjar on 17/7/1.
 */
public class StringType implements ParamType {
    @Getter
    private String value;

    public StringType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
