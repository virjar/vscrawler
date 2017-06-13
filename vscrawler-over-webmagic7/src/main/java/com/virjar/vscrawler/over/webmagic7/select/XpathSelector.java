package com.virjar.vscrawler.over.webmagic7.select;

import java.util.List;

import org.jsoup.nodes.Element;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.selector.xpath.parse.XpathParser;
import com.virjar.vscrawler.core.selector.xpath.model.JXNode;

import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.selector.BaseElementSelector;

/**
 * Created by virjar on 17/5/19.
 */
@Slf4j
public class XpathSelector extends BaseElementSelector {
    // jsoupxpath不支持预编译,所以抽象成规则模型几乎没有意义,这个是后续需要优化的
    // private XPathEvaluator xPathEvaluator;
    private String xpathStr;

    public XpathSelector(String xpathStr) {
        // this.xPathEvaluator = Xsoup.compile(xpathStr);
        this.xpathStr = xpathStr;
    }

    @Override
    public String select(Element element) {
        try {
            List<JXNode> jns = XpathParser.compile(xpathStr).evaluate(element);
            if (jns.size() == 0) {
                return null;
            }
            JXNode jxNode = jns.get(0);
            if (jxNode.isText()) {
                return jxNode.getTextVal();
            } else {
                return jxNode.getElement().ownText();
            }
        } catch (Exception e) {
            String msg = "please check the xpath syntax";
            log.error(msg);
            return null;
            // throw new XpathSyntaxErrorException(msg);
        }
    }

    @Override
    public List<String> selectList(Element element) {
        try {
            List<JXNode> jns = XpathParser.compile(xpathStr).evaluate(element);

            return Lists.transform(jns, new Function<JXNode, String>() {
                @Override
                public String apply(JXNode input) {
                    if (input.isText()) {
                        return input.getTextVal();
                    } else {
                        return input.getElement().ownText();
                    }
                }
            });

        } catch (Exception e) {
            String msg = "please check the xpath syntax";
            log.error(msg);
            return null;
            // throw new XpathSyntaxErrorException(msg);
        }
    }

    @Override
    public Element selectElement(Element element) {
        List<Element> elements = selectElements(element);
        if (elements != null && !elements.isEmpty()) {
            return elements.get(0);
        }
        return null;
    }

    @Override
    public List<Element> selectElements(final Element element) {

        try {
            List<JXNode> jns = XpathParser.compile(xpathStr).evaluate(element);

            return Lists.transform(jns, new Function<JXNode, Element>() {
                @Override
                public Element apply(JXNode input) {
                    if (input.isText()) {
                        return null;
                    } else {
                        return input.getElement();
                    }
                }
            });

        } catch (Exception e) {
            String msg = "please check the xpath syntax";
            log.error(msg);
            return null;
            // throw new XpathSyntaxErrorException(msg);
        }
    }

    @Override
    public boolean hasAttribute() {
        return false;
    }
}
