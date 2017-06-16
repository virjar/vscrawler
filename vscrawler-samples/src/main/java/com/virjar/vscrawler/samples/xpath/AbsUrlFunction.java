package com.virjar.vscrawler.samples.xpath;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.Lists;
import com.virjar.sipsoup.function.select.SelectFunction;
import com.virjar.sipsoup.model.SIPNode;
import com.virjar.sipsoup.model.XpathNode;

/**
 * Created by virjar on 17/6/16.
 */
public class AbsUrlFunction implements SelectFunction {
    @Override
    public List<SIPNode> call(XpathNode.ScopeEm scopeEm, Elements elements, List<String> args) {
        List<SIPNode> ret = Lists.newLinkedList();
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

    private void handle(boolean allAttr, String attrKey, Element element, List<SIPNode> ret) {
        if (allAttr) {
            for (Attribute attribute : element.attributes()) {
                ret.add(SIPNode.t(element.absUrl(attribute.getKey())));
            }
        } else {
            String value = element.absUrl(attrKey);
            if (StringUtils.isNotBlank(value)) {
                ret.add(SIPNode.t(value));
            }
        }
    }

    @Override
    public String getName() {
        return "absUrl";
    }
}
