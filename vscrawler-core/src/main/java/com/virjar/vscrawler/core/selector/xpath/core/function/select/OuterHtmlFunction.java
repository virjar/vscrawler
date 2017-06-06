package com.virjar.vscrawler.core.selector.xpath.core.function.select;

import java.util.LinkedList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.virjar.vscrawler.core.selector.xpath.model.JXNode;

/**
 * Created by virjar on 17/6/6.
 * 
 * @author virjar
 * @since 0.0.1 获取全部节点的 包含节点本身在内的全部html
 */
public class OuterHtmlFunction implements SelectFunction {
    @Override
    public List<JXNode> call(Elements elements) {
        List<JXNode> res = new LinkedList<JXNode>();
        if (elements != null && elements.size() > 0) {
            for (Element e : elements) {
                res.add(JXNode.t(e.outerHtml()));
            }
        }
        return res;
    }

    @Override
    public String getName() {
        return "outerHtml";
    }
}
