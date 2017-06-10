package com.virjar.vscrawler.core.selector.xpath.model;

import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by virjar on 17/6/9.
 */
public class XpathChain {
    @Getter
    @Setter
    /**
     * xpath最终的数据,可能是节点集或者字符集
     */
    private JXNode.NodeType finalType;

    @Getter
    private LinkedList<XpathNode> xpathNodeList = Lists.newLinkedList();
}
