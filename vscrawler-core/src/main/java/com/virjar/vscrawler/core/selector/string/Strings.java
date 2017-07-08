package com.virjar.vscrawler.core.selector.string;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by virjar on 17/7/8.
 */
public class Strings extends LinkedList<String> {
    public Strings(String data) {
        add(data);
    }

    public Strings(Collection<String> strings) {
        addAll(strings);
    }
}
