package com.virjar.vscrawler.core.selector.table.impl;

import com.google.common.collect.HashBasedTable;
import com.virjar.vscrawler.core.selector.table.ItemsTablePaser;
import com.virjar.vscrawler.core.selector.table.KeyResolver;
import com.virjar.vscrawler.core.selector.table.ValueResolver;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Node;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mario1oreo on 2017/6/3.
 */
public class ItemsTablePaserImpl implements ItemsTablePaser {

    private KeyResolver keyResolverImpl;
    private ValueResolver valueResolverImpl;

    public ItemsTablePaserImpl(KeyResolver keyResolverImpl, ValueResolver valueResolverImpl) {
        this.keyResolverImpl = keyResolverImpl;
        this.valueResolverImpl = valueResolverImpl;
    }

    /**
     * nodeTable是一个n*m的node矩阵
     * 默认实现的情况是一个key后面跟一个value
     * @param nodeTable
     * @return
     */
    @Override
    public Collection<String> parser(HashBasedTable nodeTable) {
        Map<String, String> targetResult = new HashMap<String, String>();
        int rowNum = nodeTable.rowMap().size();
        String parserKey = StringUtils.EMPTY;
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < nodeTable.row(i).size(); j++) {
//                单数行为名称
                if (j % 2 == 0) {
                    Node node = (Node) nodeTable.row(i).get(j);
                    String value = node.ownerDocument().select("td").get(0).text();
                    parserKey = keyResolverImpl.keyParser(value);
//                    双数为value
                } else {
                    Node node = (Node) nodeTable.row(i).get(j);
                    String value = node.ownerDocument().select("td").get(0).text();
                    targetResult.put(valueResolverImpl.valueParser(parserKey), value);
                }
            }
        }
        //TODO map转成collectio
        return null;
    }
}
