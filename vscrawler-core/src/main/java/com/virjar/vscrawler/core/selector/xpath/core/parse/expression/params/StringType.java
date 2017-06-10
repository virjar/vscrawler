package com.virjar.vscrawler.core.selector.xpath.core.parse.expression.params;

import lombok.Getter;

/**
 * Created by virjar on 17/6/10.
 */
public class StringType implements ParamType {
    @Getter
    private String data;

    public StringType(String data) {
        this.data = data;
    }
}
