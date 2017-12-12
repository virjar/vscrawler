package com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven;

import com.virjar.vscrawler.core.selector.combine.AbstractSelectable;

/**
 * Created by virjar on 2017/12/10.
 * *
 *
 * @author virjar
 * @since 0.2.1
 */
public interface ModelSelector {
    AbstractSelectable select(AbstractSelectable abstractSelectable);
}
