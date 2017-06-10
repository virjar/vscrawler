package com.virjar.vscrawler.core.selector.xpath.core.parse.expression.params;

import com.virjar.vscrawler.core.selector.xpath.core.function.filter.FilterFunction;

import lombok.Getter;

/**
 * Created by virjar on 17/6/10.
 */
public class FunctionType implements ParamType {
    @Getter
    private FilterFunction filterFunction;

    public FunctionType(FilterFunction filterFunction) {
        this.filterFunction = filterFunction;
    }
}
