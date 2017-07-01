package com.virjar.vscrawler.core.selector.combine.convert;

import com.virjar.vscrawler.core.selector.combine.AbstractSelectable;

/**
 * Created by virjar on 17/6/30.
 */
public interface NodeConvert<F extends AbstractSelectable, T extends AbstractSelectable> {
    T convert(F from);
}
