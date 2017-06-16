package com.virjar.vscrawler.samples.xpath;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.virjar.sipsoup.function.select.SelectFunction;
import com.virjar.sipsoup.model.SIPNode;
import com.virjar.sipsoup.model.XpathNode;

/**
 * Created by virjar on 17/6/16.
 */
public class MyTagSelectFunction implements SelectFunction {
    @Override
    public List<SIPNode> call(XpathNode.ScopeEm scopeEm, Elements elements, List<String> args) {
        String tagName = args.get(0);
        List<Element> temp = Lists.newLinkedList();

        if (scopeEm == XpathNode.ScopeEm.RECURSIVE || scopeEm == XpathNode.ScopeEm.CURREC) {// 递归模式
            if ("*".equals(tagName)) {
                for (Element element : elements) {
                    temp.addAll(element.getAllElements());
                }
                // 应该是在子节点查找,本节点应该忽略
                // temp.addAll(elements);
            } else {
                temp.addAll(elements.select(tagName));
            }
        } else {// 直接子代查找
            if ("*".equals(tagName)) {
                for (Element element : elements) {
                    temp.addAll(element.children());
                }
                // temp.addAll(elements);
            } else {
                for (Element element : elements) {
                    for (Element child : element.children()) {
                        if (StringUtils.equals(child.tagName(), tagName)) {
                            temp.add(child);
                        }
                    }
                }
            }
        }

        return Lists.transform(temp, new Function<Element, SIPNode>() {
            @Override
            public SIPNode apply(Element input) {
                return SIPNode.e(input);
            }
        });
    }

    @Override
    public String getName() {
        return "tag";
    }
}
