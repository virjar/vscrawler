package com.virjar.vscrawler.core.selector.xpath.core.function.select;

import java.util.LinkedList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.virjar.vscrawler.core.selector.xpath.model.JXNode;

/**
 * Created by virjar on 17/6/6.
 * 
 * @since 0.0.1
 * @author virjar 递归获取节点内全部的纯文本
 */
public class AllTextFunction implements SelectFunction {
    @Override
    public List<JXNode> call(Elements elements) {
        List<JXNode> res = new LinkedList<JXNode>();
        if (elements != null && elements.size() > 0) {
            for (Element e : elements) {
                res.add(JXNode.t(e.text()));
            }
        }
        return res;
    }

    @Override
    public String getName() {
        return "allText";
    }
}
