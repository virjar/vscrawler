package com.virjar.vscrawler.core.selector.table;

import org.jsoup.nodes.Element;

/**
 * table中的key  通过keyResolver转换后得到的keyR
 * 通过valueResolver转换成目标存储结构的KeyV
 * Created by mario1oreo on 2017/6/3.
 */
public interface ValueResolver {

    String resolveValue(Element element);
}
