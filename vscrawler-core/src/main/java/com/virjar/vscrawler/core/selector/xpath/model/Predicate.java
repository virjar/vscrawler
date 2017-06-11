package com.virjar.vscrawler.core.selector.xpath.model;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;

import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.SyntaxNode;
import com.virjar.vscrawler.core.selector.xpath.util.XpathUtil;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * xpath语法节点的谓语部分，即要满足的限定条件
 * 
 * @author virjar
 * @since 0.0.1
 */
@Slf4j
public class Predicate {

    private SyntaxNode syntaxNode;
    @Getter
    private String predicateStr;

    public Predicate(String predicateStr, SyntaxNode syntaxNode) {
        this.predicateStr = predicateStr;
        this.syntaxNode = syntaxNode;
    }

    public boolean isValid(Element element) {
        if (element == null) {
            return false;
        }
        Object ret = syntaxNode.calc(element);
        if (ret == null) {
            return false;
        }

        if (ret instanceof Number) {
            int i = ((Number) ret).intValue();
            return XpathUtil.getElIndexInSameTags(element) == i;
        }

        if (ret instanceof Boolean) {
            return (boolean) ret;
        }

        if (ret instanceof CharSequence) {
            String s = ret.toString();
            Boolean booleanValue = BooleanUtils.toBooleanObject(s);
            if (booleanValue != null) {
                return booleanValue;
            }
            return StringUtils.isNotBlank(s);
        }

        log.warn("can not recognize predicate expression calc result:" + ret);
        return false;
    }
}
