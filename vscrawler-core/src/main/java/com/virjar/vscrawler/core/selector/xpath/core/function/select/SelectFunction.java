package com.virjar.vscrawler.core.selector.xpath.core.function.select;

import java.util.List;

import com.virjar.vscrawler.core.selector.xpath.core.function.NameAware;
import org.jsoup.select.Elements;

import com.virjar.vscrawler.core.selector.xpath.model.JXNode;

/**
 * Created by virjar on 17/6/6.
 */
public interface SelectFunction extends NameAware {
    List<JXNode> call(Elements elements);

}
