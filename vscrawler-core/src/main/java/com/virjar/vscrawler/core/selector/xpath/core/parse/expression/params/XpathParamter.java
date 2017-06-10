package com.virjar.vscrawler.core.selector.xpath.core.parse.expression.params;

import com.virjar.vscrawler.core.selector.xpath.model.XpathEvaluator;

import lombok.Getter;

/**
 * Created by virjar on 17/6/10.
 */
public class XpathParamter implements ParamType {
    @Getter
    private XpathEvaluator xpathEvaluator;

    public XpathParamter(XpathEvaluator xpathEvaluator) {
        this.xpathEvaluator = xpathEvaluator;
    }
}
