package com.virjar.vscrawler.core.selector.xpath.core.function.filter;

import org.jsoup.nodes.Element;

import com.virjar.vscrawler.core.selector.xpath.util.CommonUtil;

/**
 * Created by virjar on 17/6/6.
 * 
 * @author virjar
 * @since 0.0.1 判断一个元素是不是最后一个同名同胞中的
 */
public class LastFunction implements FilterFunction {
    @Override
    public Object call(Element element) {
        return CommonUtil.getElIndexInSameTags(element) == CommonUtil.sameTagElNums(element);
    }

    @Override
    public String getName() {
        return "last";
    }
}
