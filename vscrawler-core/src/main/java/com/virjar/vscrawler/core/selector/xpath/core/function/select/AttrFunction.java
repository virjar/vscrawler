package com.virjar.vscrawler.core.selector.xpath.core.function.select;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.selector.xpath.model.JXNode;
import com.virjar.vscrawler.core.selector.xpath.model.XpathNode;

/**
 * Created by virjar on 17/6/11.
 */
public class AttrFunction implements SelectFunction {
    @Override
    public List<JXNode> call(XpathNode.ScopeEm scopeEm, Elements elements, List<String> args) {
        List<JXNode> ret = Lists.newLinkedList();
        boolean allAttr = StringUtils.equals(args.get(0), "*");
        String attrName = args.get(0);
        for (Element element : elements) {
            handle(allAttr, attrName, element, ret);
            if (scopeEm == XpathNode.ScopeEm.RECURSIVE || scopeEm == XpathNode.ScopeEm.CURREC) {
                Elements allElements = element.getAllElements();
                for (Element subElement : allElements) {
                    handle(allAttr, attrName, subElement, ret);
                }
            }
        }
        return ret;
    }

    private void handle(boolean allAttr, String attrKey, Element element, List<JXNode> ret) {
        if (allAttr) {
            ret.add(JXNode.t(element.attributes().toString()));
        } else {
            String value = element.attr(attrKey);
            if (StringUtils.isNotBlank(value)) {
                ret.add(JXNode.t(value));
            }
        }
    }

    @Override
    public String getName() {
        return "@";
    }
}
