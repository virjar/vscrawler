package com.virjar.vscrawler.core.selector.table;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by virjar on 17/6/20.<br/>
 * 注意,线程不安全
 */
public class Table<T> {
    private int columns = 0;
    private List<List<T>> data = Lists.newArrayList();

    @Getter
    @Setter
    private List<T> tableHeader = Lists.newArrayList();

    @Getter
    @Setter
    private List<String> tableHeaderKeys = Lists.newArrayList();

    public Table() {
    }

    public Table(List<List<T>> data) {
        for (List<T> row : data) {
            addRow(row);
        }
    }

    public void addRow(List<T> row) {
        if (columns < row.size()) {
            columns = row.size();
        }
        data.add(row);
    }

    public void addColumn(List<T> column) {
        int i = 0;
        for (T t : column) {
            List<T> ts = data.get(i);
            for (int j = ts.size() - 1; j < columns; j++) {
                ts.add(null);
            }
            ts.add(t);
        }
        columns++;
    }

    public int rowSize() {
        return data.size();
    }

    public int columnSize() {
        return columns;
    }

    public T cell(int row, int column) {
        return data.get(row).get(column);
    }

    public List<T> row(int row) {
        return data.get(row);
    }

    public List<T> column(final int column) {
        return Lists.transform(data, new Function<List<T>, T>() {
            @Override
            public T apply(List<T> input) {
                return input.get(column);
            }
        });
    }
}
