package com.virjar.vscrawler.serialize;

import java.util.Collection;

/**
 * Created by virjar on 17/4/16.
 */
public interface Pipline {
    void saveItem(Collection<String> itemJson);
}
