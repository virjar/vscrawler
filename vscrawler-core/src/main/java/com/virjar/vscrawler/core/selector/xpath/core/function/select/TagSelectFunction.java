package com.virjar.vscrawler.core.selector.xpath.core.function.select;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.selector.xpath.model.JXNode;
import com.virjar.vscrawler.core.selector.xpath.model.XpathNode;

/**
 * Created by virjar on 17/6/11.
 */
public class TagSelectFunction implements SelectFunction {
    @Override
    public List<JXNode> call(XpathNode.ScopeEm scopeEm, Elements elements, List<String> args) {
        String tagName = args.get(0);
        List<Element> temp = Lists.newLinkedList();

        if (scopeEm == XpathNode.ScopeEm.RECURSIVE || scopeEm == XpathNode.ScopeEm.CURREC) {// 递归模式
            if ("*".equals(tagName)) {
                for (Element element : elements) {
                    temp.addAll(element.getAllElements());
                }
            } else {
                temp.addAll(elements.select(tagName));
            }
        } else {// 直接子代查找
            if ("*".equals(tagName)) {
                for (Element element : elements) {
                    temp.addAll(element.children());
                }
            } else {
                for (Element element : elements) {
                    if (StringUtils.equals(element.tagName(), tagName)) {
                        temp.add(element);
                    }
                }
            }
        }

        return Lists.transform(temp, new Function<Element, JXNode>() {
            @Override
            public JXNode apply(Element input) {
                return JXNode.e(input);
            }
        });
    }

    @Override
    public String getName() {
        return "tag";
    }
}
