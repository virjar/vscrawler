package com.virjar.vscrawler.core.selector.string.tree;

import lombok.Getter;

import java.util.List;

/**
 * Created by virjar on 17/7/1.
 */
public class FunctionType implements ParamType {
    @Getter
    private StringFunction stringFunction;
    @Getter
    private List<ParamType> params;

    public FunctionType(StringFunction stringFunction, List<ParamType> params) {
        this.stringFunction = stringFunction;
        this.params = params;
    }
}
