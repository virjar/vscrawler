package com.virjar.vscrawler.core.selector.string.tree;

import lombok.Getter;

/**
 * Created by virjar on 17/7/1.
 */
public class IntegerType implements ParamType {
    @Getter
    private int value;

    public IntegerType(int value) {
        this.value = value;
    }

}
