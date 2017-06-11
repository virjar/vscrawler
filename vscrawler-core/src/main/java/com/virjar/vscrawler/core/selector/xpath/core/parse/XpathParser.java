package com.virjar.vscrawler.core.selector.xpath.core.parse;

import com.virjar.vscrawler.core.selector.xpath.exception.XpathSyntaxErrorException;
import com.virjar.vscrawler.core.selector.xpath.model.XpathEvaluator;

import lombok.Getter;

/**
 * Created by virjar on 17/6/9.
 */
public class XpathParser {
    @Getter
    private String xpathStr;
    private TokenQueue tokenQueue;

    public XpathParser(String xpathStr) {
        this.xpathStr = xpathStr;
        tokenQueue = new TokenQueue(xpathStr);
    }

    public XpathEvaluator parse() throws XpathSyntaxErrorException {
        XpathStateMachine xpathStateMachine = new XpathStateMachine(tokenQueue);
        while (xpathStateMachine.getState() != XpathStateMachine.BuilderState.END) {
            xpathStateMachine.getState().parse(xpathStateMachine);
        }
        return xpathStateMachine.getEvaluator();
    }
}
