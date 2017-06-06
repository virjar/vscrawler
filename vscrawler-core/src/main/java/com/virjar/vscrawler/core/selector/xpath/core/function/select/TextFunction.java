package com.virjar.vscrawler.core.selector.xpath.core.function.select;

import com.virjar.vscrawler.core.selector.xpath.model.JXNode;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by virjar on 17/6/6.
 */
public class TextFunction implements SelectFunction {
    /**
     * 只获取节点自身的子文本
     * @param elements
     * @return
     */
    @Override
    public List<JXNode> call(Elements elements) {
        List<JXNode> res = new LinkedList<JXNode>();
        if (elements!=null&&elements.size()>0){
            for (Element e:elements){
                if (e.nodeName().equals("script")){
                    res.add(JXNode.t(e.data()));
                }else {
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
