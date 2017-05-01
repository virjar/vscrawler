package com.virjar.vscrawler.serialize;

import java.util.Collection;

/**
 * Created by virjar on 17/4/16.
 */
public class ConsolePipline implements Pipline {
    @Override
    public void saveItem(Collection<String> itemJson) {
        for (String str : itemJson) {
            System.out.println(str);
        }
    }
}