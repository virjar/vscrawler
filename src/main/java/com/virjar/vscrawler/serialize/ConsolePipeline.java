package com.virjar.vscrawler.serialize;

import java.util.Collection;

/**
 * Created by virjar on 17/4/16.
 * @author virjar
 * @since 0.0.1
 */
public class ConsolePipeline implements Pipeline {
    @Override
    public void saveItem(Collection<String> itemJson) {
        for (String str : itemJson) {
            System.out.println(str);
        }
    }
}
