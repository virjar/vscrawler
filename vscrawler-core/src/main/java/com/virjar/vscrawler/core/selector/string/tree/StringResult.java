package com.virjar.vscrawler.core.selector.string.tree;

import java.util.List;

import lombok.Getter;

/**
 * Created by virjar on 17/7/1.
 */
public class StringResult implements StringFunctionResult {
    @Getter
    private String value;

    public StringResult(String value) {
        this.value = value;
    }

    public FunctionNode toFunctionNode(){
        return new StringFunctionNode();
    }

    public class StringFunctionNode implements FunctionNode {
        @Override
        public StringFunctionResult call(String from, List<FunctionNode> params) {
            return StringResult.this;
        }
    }
}
