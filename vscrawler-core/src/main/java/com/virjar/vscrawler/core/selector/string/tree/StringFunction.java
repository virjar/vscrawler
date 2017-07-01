package com.virjar.vscrawler.core.selector.string.tree;

import com.virjar.sipsoup.function.NameAware;

import java.util.List;

/**
 * Created by virjar on 17/7/1.
 */
public interface StringFunction extends NameAware{
    List<String> call(String from, List<ParamType> params);
}
