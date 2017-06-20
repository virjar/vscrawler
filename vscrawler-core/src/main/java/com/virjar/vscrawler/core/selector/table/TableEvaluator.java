package com.virjar.vscrawler.core.selector.table;

import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Element;

/**
 * Created by virjar on 17/6/20.表格抽取器,对表格数据执行一个抽取动作,得到一个模型数据
 */
public abstract class TableEvaluator {
    abstract List<Map<String, String>> evaluate(Table<Element> table);
}
