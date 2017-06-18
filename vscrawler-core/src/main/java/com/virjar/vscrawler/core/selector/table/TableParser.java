package com.virjar.vscrawler.core.selector.table;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.virjar.sipsoup.parse.XpathParser;

/**
 * Created by virjar on 17/6/17.<br/>
 * parse a table data from a document,guava这个table一点儿都不好用,求大神给一个好用的
 */
public class TableParser {
    public static Table<Integer, Integer, Element> parse(Element element) {
        Elements table = element.getElementsByTag("table");
        if (table.size() == 0) {
            return HashBasedTable.create();
        }
        return innerParse(table.first(), null, null);
    }

    public static Table<Integer, Integer, Element> parseByTableID(Element element, String tableID) {
        Element tableElement = element.getElementById(tableID);
        if (tableElement == null) {
            return HashBasedTable.create();
        }
        return innerParse(tableElement, null, null);
    }

    public static Table<Integer, Integer, Element> parseBySelector(Element element, String tableSelector) {
        return parseBySelector(element, tableSelector, null, null);
    }

    public static Table<Integer, Integer, Element> parseBySelector(Element element, String tableSelector,
            String rowSelector, String columnSelector) {
        Elements elements = XpathParser.compileNoError(tableSelector).evaluateToElements(element);
        if (elements.size() == 0) {
            return HashBasedTable.create();
        }
        return innerParse(elements.first(), rowSelector, columnSelector);
    }

    private static Table<Integer, Integer, Element> innerParse(Element tableNode, String rowSelector,
            String columnSelector) {
        HashBasedTable<Integer, Integer, Element> ret = HashBasedTable.create();
        if (StringUtils.isEmpty(rowSelector)) {
            rowSelector = "/css('tr')::self()";
        }
        if (StringUtils.isEmpty(columnSelector)) {
            columnSelector = "/css('td')::self()";
        }
        Elements rows = XpathParser.compileNoError(rowSelector).evaluateToElements(tableNode);

        for (int i = 0; i < rows.size(); i++) {
            Element element = rows.get(i);
            Elements columns = XpathParser.compileNoError(columnSelector).evaluateToElements(element);
            for (int j = 0; j < columns.size(); j++) {
                ret.put(i, j, columns.get(i));
            }
        }
        return ret;
    }

}
