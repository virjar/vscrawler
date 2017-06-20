package com.virjar.vscrawler.core.selector.table;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.virjar.sipsoup.parse.XpathParser;

/**
 * Created by virjar on 17/6/17.<br/>
 * parse a table data from a document,guava这个table一点儿都不好用,求大神给一个好用的
 */
public class TableParser {
    private String tableSelector;
    private String rowSelector;
    private String columnSelector;
    private Element tableElement;
    private String thSelector;
    private List<Element> tableHeader;

    public TableParser(String columnSelector, String rowSelector, Element tableElement, List<Element> tableHeader,
            String tableSelector, String thSelector) {
        this.columnSelector = columnSelector;
        this.rowSelector = rowSelector;
        this.tableElement = tableElement;
        this.tableHeader = tableHeader;
        this.tableSelector = tableSelector;
        this.thSelector = thSelector;
    }

    public static class TableParserBuilder {
        private String tableSelector;
        private String rowSelector;
        private String columnSelector;
        private Element tableElement;
        private String thSelector;
        private List<Element> tableHeader;

        public TableParser build() {
            if (tableElement == null && StringUtils.isEmpty(tableSelector)) {
                tableSelector = "/css('table')::self()";// 效率优于 //table
            }
            if (StringUtils.isEmpty(rowSelector)) {
                rowSelector = "//tr";
            }
            if (StringUtils.isEmpty(columnSelector)) {
                columnSelector = "//td";
            }
            return new TableParser(columnSelector, rowSelector, tableElement, tableHeader, tableSelector, thSelector);
        }

        public TableParserBuilder setColumnSelector(String columnSelector) {
            this.columnSelector = columnSelector;
            return this;
        }

        public TableParserBuilder setRowSelector(String rowSelector) {
            this.rowSelector = rowSelector;
            return this;
        }

        public TableParserBuilder setTableElement(Element tableElement) {
            this.tableElement = tableElement;
            return this;
        }

        public TableParserBuilder setTableHeader(List<Element> tableHeader) {
            this.tableHeader = tableHeader;
            return this;
        }

        public TableParserBuilder setTableSelector(String tableSelector) {
            this.tableSelector = tableSelector;
            return this;
        }

        public TableParserBuilder setThSelector(String thSelector) {
            this.thSelector = thSelector;
            return this;
        }

    }

    public Table<Element> parse(Element table) {
        // step 1,judge table element
        if (table == null) {
            table = XpathParser.compileNoError(tableSelector).evaluateToElements(tableElement).first();
        }

        // step 2 ,evaluate rows
        Elements rows = XpathParser.compileNoError(rowSelector).evaluateToElements(table);
        Table<Element> ret = new Table<>();
        for (Element row : rows) {
            ret.addRow(XpathParser.compileNoError(columnSelector).evaluateToElement(row));
        }

        // step 3 ,headers
        List<Element> headers = tableHeader;
        if ((headers == null || headers.isEmpty()) && StringUtils.isNotEmpty(thSelector)) {
            headers = XpathParser.compileNoError(thSelector).evaluateToElement(table);
        }
        if (headers != null) {
            ret.setTableHeader(headers);
        }
        return ret;
    }

    public Table<Element> parse() {
        return parse(null);
    }

}
