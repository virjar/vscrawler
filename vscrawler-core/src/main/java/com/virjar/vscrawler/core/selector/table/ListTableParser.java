package com.virjar.vscrawler.core.selector.table;

import com.google.common.collect.HashBasedTable;

import java.util.Collection;

/**
 *
 * 解析列表类型的table
 * key      key1    key2    key3    key4
 * value    value1  value2  value3  value4
 * value    value1  value2  value3  value4
 *
 * Created by mario1oreo on 2017/6/3.
 */
public interface ListTableParser {
    Collection<String> parser(HashBasedTable nodeTable);
}
