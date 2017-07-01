package com.virjar.vscrawler.core.selector.string.tree;

import java.util.List;

import lombok.Getter;

/**
 * Created by virjar on 17/7/1.
 */
public class IntergerResult implements StringFunctionResult {
    @Getter
    private int value;

    public IntergerResult(int value) {
        this.value = value;
    }

    public FunctionNode toFunctionNode() {
        return new IntegerNode();
    }

    public class IntegerNode implements FunctionNode {

        @Override
        public StringFunctionResult call(String from, List<FunctionNode> params) {
            return IntergerResult.this;
        }
    }
}
