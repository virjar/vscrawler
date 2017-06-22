package com.virjar.vscrawler.core.selector.table;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Element;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Created by virjar on 17/6/20.表格抽取器,对表格数据执行一个抽取动作,得到一个模型数据
 */
public abstract class TableEvaluator {
    protected KeyResolver keyResolver;
    protected ValueResolver valueResolver;

    public TableEvaluator(KeyResolver keyResolver, ValueResolver valueResolver) {
        this.keyResolver = keyResolver;
        this.valueResolver = valueResolver;
    }

    public static List<Map<String, String>> toMap(List<List<Pair<String, String>>> input) {
        return Lists.transform(input, new Function<List<Pair<String, String>>, Map<String, String>>() {
            @Override
            public Map<String, String> apply(List<Pair<String, String>> input) {
                Map<String, String> ret = Maps.newHashMap();
                for (Pair<String, String> pair : input) {
                    ret.put(pair.getLeft(), pair.getValue());
                }
                return ret;
            }
        });
    }

    public static JSONArray toJson(List<List<Pair<String, String>>> input) {
        JSONArray jsonArray = new JSONArray();
        for (List<Pair<String, String>> pairs : input) {
            JSONObject itemJson = new JSONObject();
            for (Pair<String, String> pair : pairs) {
                itemJson.put(pair.getLeft(), pair.getRight());
            }
            jsonArray.add(itemJson);
        }

        return jsonArray;
    }

    public JSONArray evaluateToJson(Table<Element> table) {
        return toJson(evaluate(table));
    }

    public List<Map<String, String>> evaluateToMap(Table<Element> table) {
        return toMap(evaluate(table));
    }

    public abstract List<List<Pair<String, String>>> evaluate(Table<Element> table);

    public static TableEvaluator createPairEvaluator() {
        return createPairEvaluator(null, null, null);
    }

    public static TableEvaluator createPairEvaluator(boolean horizontal) {
        return createPairEvaluator(null, null, horizontal);
    }

    public static TableEvaluator createPairEvaluator(KeyResolver keyResolver, ValueResolver valueResolver,
            Boolean horizontal) {
        if (horizontal == null) {
            horizontal = true;
        }
        if (horizontal) {
            return new HorizontalPairTableEvaluator(wrapDefaultKeyResolver(keyResolver),
                    wrapDefaultValueResolver(valueResolver));
        } else {
            return new VerticalPairTableEvaluator(wrapDefaultKeyResolver(keyResolver),
                    wrapDefaultValueResolver(valueResolver));
        }
    }

    public static TableEvaluator createListEvaluator(KeyResolver keyResolver, ValueResolver valueResolver,
            List<String> keys) {

        return new ListTableEvaluator(wrapDefaultKeyResolver(keyResolver), wrapDefaultValueResolver(valueResolver),
                keys);
    }

    public static TableEvaluator createListEvaluator(KeyResolver keyResolver, ValueResolver valueResolver) {
        return new ListTableEvaluator(keyResolver, valueResolver, null);
    }

    public static TableEvaluator createListEvaluator(List<String> keys) {
        return new ListTableEvaluator(null, null, keys);
    }

    public static TableEvaluator createListEvaluator() {
        return createListEvaluator(null, null, null);
    }

    private static KeyResolver wrapDefaultKeyResolver(KeyResolver keyResolver) {
        if (keyResolver != null) {
            return keyResolver;
        }
        return new DefaultKeyResolver();
    }

    private static ValueResolver wrapDefaultValueResolver(ValueResolver valueResolver) {
        if (valueResolver != null) {
            return valueResolver;
        }
        return new DefaultValueResolver();
    }

    public static class TableVisitCursor {
        public static final int vertical = 0;
        public static final int horizontal = 1;

        private Table<Element> table;
        private int nowRow = 0;
        private int nowColumn = 0;
        private int direct = vertical;

        public TableVisitCursor(Table<Element> table) {
            this.table = table;
        }

        public int changeDirect(int direct) {
            Preconditions.checkArgument(direct == vertical || direct == horizontal);
            int old = this.direct;
            this.direct = direct;
            return old;
        }

        public boolean hasNext() {
            return !(direct != vertical && direct != horizontal) && nowColumn < table.columnSize()
                    && nowRow < table.rowSize();
        }

        public Element next() {
            Element element = table.cell(nowRow, nowColumn);
            advance();
            return element;
        }

        public void advance() {
            if (direct == vertical) {
                if (nowRow < table.rowSize()) {
                    nowRow++;
                } else {
                    nowRow = 0;
                    nowColumn++;
                }
            } else if (direct == horizontal) {
                if (nowColumn < table.columnSize()) {
                    nowColumn++;
                } else {
                    nowColumn = 0;
                    nowRow++;
                }
            }
        }
    }

    public interface TableVisitor {
        void visit(TableVisitCursor cursor);

        List<List<Pair<String, String>>> getData();
    }

    /**
     * 基于游标的visit扩展抽取
     */
    private class CursorTableEvaluator extends TableEvaluator {

        private TableVisitor tableVisitor;

        public CursorTableEvaluator(KeyResolver keyResolver, ValueResolver valueResolver, TableVisitor tableVisitor) {
            super(keyResolver, valueResolver);
            this.tableVisitor = tableVisitor;
        }

        @Override
        public List<List<Pair<String, String>>> evaluate(Table<Element> table) {
            TableVisitCursor cursor = new TableVisitCursor(table);
            while (cursor.hasNext()) {
                tableVisitor.visit(cursor);
            }
            return tableVisitor.getData();
        }
    }

    /**
     * 上下互为key-value
     */
    private static class VerticalPairTableEvaluator extends TableEvaluator {

        public VerticalPairTableEvaluator(KeyResolver keyResolver, ValueResolver valueResolver) {
            super(keyResolver, valueResolver);
        }

        @Override
        public List<List<Pair<String, String>>> evaluate(Table<Element> table) {
            List<Pair<String, String>> targetResult = Lists.newArrayList();

            for (int column = 0; column < table.columnSize(); column++) {
                for (int row = 0; row < table.rowSize() - 1; row += 2) {
                    targetResult.add(Pair.of(keyResolver.resolveKey(table.cell(row, column)),
                            valueResolver.resolveValue(table.cell(row + 1, column))));
                }
            }
            List<List<Pair<String, String>>> ret = Lists.newArrayList();
            ret.add(targetResult);
            return ret;
        }
    }

    /**
     * 普通左右互为key-value-pair的表格数据抽取
     */
    private static class HorizontalPairTableEvaluator extends TableEvaluator {

        public HorizontalPairTableEvaluator(KeyResolver keyResolver, ValueResolver valueResolver) {
            super(keyResolver, valueResolver);
        }

        @Override
        public List<List<Pair<String, String>>> evaluate(Table<Element> table) {
            List<Pair<String, String>> targetResult = Lists.newArrayList();
            for (int row = 0; row < table.rowSize(); row++) {
                for (int column = 0; column < table.columnSize() - 1; column += 2) {
                    targetResult.add(Pair.of(keyResolver.resolveKey(table.cell(row, column)),
                            valueResolver.resolveValue(table.cell(row, column + 1))));
                }
            }
            List<List<Pair<String, String>>> ret = Lists.newArrayList();
            ret.add(targetResult);
            return ret;
        }
    }

    /**
     * 普通列表模式的表格数据抽取
     */
    private static class ListTableEvaluator extends TableEvaluator {

        private List<String> keys = null;

        public ListTableEvaluator(KeyResolver keyResolver, ValueResolver valueResolver, List<String> keys) {
            super(keyResolver, valueResolver);
            this.keys = keys;
        }

        @Override
        public List<List<Pair<String, String>>> evaluate(Table<Element> table) {
            if (keys == null) {
                keys = table.getTableHeaderKeys();
            }
            if (keys == null) {
                keys = Lists.newArrayList();
                List<Element> tableHeader = table.getTableHeader();
                for (Element element : tableHeader) {
                    String key = keyResolver.resolveKey(element);
                    if (StringUtils.isEmpty(key)) {
                        throw new IllegalStateException(
                                "can not get value from " + keyResolver + " with element :" + element);
                    }
                    keys.add(key);
                }
            }
            if (keys == null || keys.size() == 0) {
                throw new IllegalStateException("table header is empty");
            }
            List<List<Pair<String, String>>> ret = Lists.newArrayList();
            for (int i = 0; i < table.rowSize(); i++) {
                ret.add(handleRow(table.row(i)));
            }
            return ret;
        }

        private List<Pair<String, String>> handleRow(List<Element> row) {
            List<Pair<String, String>> ret = Lists.newArrayList();

            for (int i = 0; i < row.size(); i++) {
                ret.add(Pair.of(keys.get(i), valueResolver.resolveValue(row.get(i))));
            }

            return ret;
        }
    }

}
