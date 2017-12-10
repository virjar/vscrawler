package com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven;

import com.virjar.vscrawler.core.seed.Seed;
import com.virjar.vscrawler.core.selector.combine.AbstractSelectable;
import lombok.Setter;

/**
 * Created by virjar on 2017/12/11.<br/>
 * 所有自动抽取的类都需要继承这个
 */
public abstract class AbstractAutoProcessModel {
    /**
     * 对应抓取种子
     */
    @Setter
    private Seed seed;
    /**
     * 对应原始文本
     */
    @Setter
    private String rawText;

    @Setter
    private AbstractSelectable originSelectable;


    protected boolean hasGrabSuccess() {
        return true;
    }

    protected void afterAutoFetch() {

    }
}
