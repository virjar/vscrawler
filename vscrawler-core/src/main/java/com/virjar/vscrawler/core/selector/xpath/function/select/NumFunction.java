package com.virjar.vscrawler.core.selector.xpath.function.select;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.virjar.vscrawler.core.selector.xpath.model.JXNode;
import com.virjar.vscrawler.core.selector.xpath.model.XpathNode;

/**
 * Created by virjar on 17/6/6.
 * 
 * @author virjar
 * @since 0.0.1 抽取节点自有文本中全部数字
 */
public class NumFunction implements SelectFunction {
    @Override
    public List<JXNode> call(XpathNode.ScopeEm scopeEm, Elements elements, List<String> args) {
        List<JXNode> res = new LinkedList<JXNode>();
        if (elements != null) {
            Pattern pattern = Pattern.compile("\\d+");
            for (Element e : elements) {
                Matcher matcher = pattern.matcher(e.ownText());
                if (matcher.find()) {
                    res.add(JXNode.t(matcher.group()));
                }
            }
        }
        return res;
    }

    @Override
    public String getName() {
        return "num";
    }
}
