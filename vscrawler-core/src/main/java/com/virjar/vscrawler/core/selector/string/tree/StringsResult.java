package com.virjar.vscrawler.core.selector.string.tree;

import java.util.List;

import lombok.Getter;

/**
 * Created by virjar on 17/7/1.
 */
public class StringsResult implements StringFunctionResult {
    @Getter
    private List<String> value;

    public StringsResult(List<String> value) {
        this.value = value;
    }
}
