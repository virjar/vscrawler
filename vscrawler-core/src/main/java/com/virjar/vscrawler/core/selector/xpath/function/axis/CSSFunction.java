package com.virjar.vscrawler.core.selector.xpath.function.axis;

import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by virjar on 17/6/8.
 * 
 * @since 0.0.1
 * @author virjar 将css Query的功能,变成一个xpath的轴函数
 */
public class CSSFunction implements AxisFunction {
    @Override
    public Elements call(Element e, List<String> args) {
        return e.select(args.get(0));
    }

    @Override
    public String getName() {
        return "css";
    }
}
