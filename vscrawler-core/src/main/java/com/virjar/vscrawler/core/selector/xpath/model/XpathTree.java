package com.virjar.vscrawler.core.selector.xpath.model;

import java.util.List;

import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.selector.xpath.exception.FinalTypeNotSameException;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by virjar on 17/6/9.
 */
public class XpathTree {
    private JXNode.NodeType finalNodeType;

    @Getter
    private List<XpathChain> orNodes = Lists.newLinkedList();

    @Getter
    @Setter
    private String originXpathStr;

    private void check() throws FinalTypeNotSameException {
        for (XpathChain chain : orNodes) {
            if (finalNodeType == null) {
                finalNodeType = chain.getFinalType();
            } else if (finalNodeType != chain.getFinalType()) {
                throw new FinalTypeNotSameException("all xpath node final must get same result type");
            }
        }
    }

}
