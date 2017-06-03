package com.virjar.vscrawler.core.selector.table;

import com.google.common.collect.HashBasedTable;

import java.util.Collection;

/**
 * 解析明细类型的table
 * key value  key1 value1 key2 value2
 * key3 value3  key4 value4 key5 value5
 *
 * Created by mario1oreo on 2017/6/3.
 */
public interface ItemsTablePaser {
    Collection<String> parser(HashBasedTable nodeTable);
}
