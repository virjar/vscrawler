package com.virjar.vscrawler.core.selector.xpath.core.function.select;

import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.selector.xpath.model.JXNode;
import com.virjar.vscrawler.core.selector.xpath.model.XpathNode;

/**
 * Created by virjar on 17/6/6.
 */
public class TextFunction implements SelectFunction {
    /**
     * 只获取节点自身的子文本
     * 
     * @param elements
     * @return
     */
    @Override
    public List<JXNode> call(XpathNode.ScopeEm scopeEm, Elements elements, List<String> args) {
        List<JXNode> res = Lists.newLinkedList();
        if (elements != null && elements.size() > 0) {
            for (Element e : elements) {
                if (e.nodeName().equals("script")) {
                    res.add(JXNode.t(e.data()));
                } else {
                    res.add(JXNode.t(e.ownText()));
                }
            }
        }
        return res;
    }

    @Override
    public String getName() {
        return "text";
    }
}
