package com.virjar.vscrawler.core.selector.string.tree;

import java.util.List;

import com.virjar.vscrawler.core.selector.string.FunctionEnv;

/**
 * Created by virjar on 17/7/1.
 */
public class FunctionResult implements StringFunctionResult {
    private String funtionName;
    private List<FunctionNode> params;

    public FunctionResult(String functionName, List<FunctionNode> params) {
        this.funtionName = functionName;
        this.params = params;
    }

    public FunctionNode toFunctionNode() {
        return new FunctionNodeNode();
    }

    public class FunctionNodeNode implements FunctionNode {

        @Override
        public StringFunctionResult call(String from, List<FunctionNode> params) {
            return FunctionEnv.findFunction(funtionName).call(from, params);
        }
    }
}
