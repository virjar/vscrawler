package com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven;

import com.virjar.vscrawler.core.selector.combine.AbstractSelectable;

/**
 * Created by virjar on 2017/12/10.
 */
public interface ModelSelector {
    AbstractSelectable select(AbstractSelectable abstractSelectable);
}
