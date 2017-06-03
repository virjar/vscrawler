package com.virjar.vscrawler.core.selector.table.impl;

import com.google.common.collect.HashBasedTable;
import com.virjar.vscrawler.core.selector.table.KeyResolver;
import com.virjar.vscrawler.core.selector.table.ListTableParser;
import com.virjar.vscrawler.core.selector.table.ValueResolver;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Node;

import java.util.*;

/**
 * Created by mario1oreo on 2017/6/3.
 */
public class ListTablePaserImpl implements ListTableParser {

    private KeyResolver keyResolverImpl;
    private ValueResolver valueResolverImpl;

    public ListTablePaserImpl(KeyResolver keyResolverImpl, ValueResolver valueResolverImpl) {
        this.keyResolverImpl = keyResolverImpl;
        this.valueResolverImpl = valueResolverImpl;
    }

    @Override
    public Collection<String> parser(HashBasedTable nodeTable) {
        List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
        Map<Integer, String> keyMap = new HashMap<Integer, String>();
        int rowNum = nodeTable.rowMap().size();
        for (int i = 0; i < rowNum; i++) {
            //这一行是key
            if (i == 0) {
                for (int j = 0; j < nodeTable.row(0).size(); j++) {
                    Node node = (Node) nodeTable.row(0).get(j);
                    String value = node.ownerDocument().select("td").get(0).text();
                    String parserKey = keyResolverImpl.keyParser(value);
                    keyMap.put(j, parserKey);
                }
            //这是value
            }else {
                for (int j = 0; j < nodeTable.row(i).size(); j++) {
                    Node node = (Node) nodeTable.row(i).get(j);
                    String value = node.ownerDocument().select("td").get(0).text();
                    Map<String, String> map = new HashMap<String, String>();
                    map.put(valueResolverImpl.valueParser(keyMap.get(j)), value);
                    resultList.add(map);
                    }
                }
            }
            //TODO resultList转成 Collection<String>
        return null;
    }
}
