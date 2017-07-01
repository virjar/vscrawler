package com.virjar.vscrawler.core.selector.combine;

import com.virjar.vscrawler.core.selector.combine.selectables.RawNode;

/**
 * Created by virjar on 17/6/30.
 */
public class Selector {
    public static AbstractSelectable rowText(String rowText, String baseUrl) {
        return new RawNode(rowText, baseUrl);
    }

}
