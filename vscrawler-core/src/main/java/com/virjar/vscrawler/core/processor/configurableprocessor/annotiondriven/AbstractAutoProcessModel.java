package com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven;

import com.alibaba.fastjson.annotation.JSONField;
import com.virjar.vscrawler.core.seed.Seed;
import com.virjar.vscrawler.core.selector.combine.AbstractSelectable;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

/**
 * Created by virjar on 2017/12/11.<br/>
 * 所有自动抽取的类都需要继承这个
 * *
 *
 * @author virjar
 * @since 0.2.1
 */
public abstract class AbstractAutoProcessModel {
    /**
     * 对应抓取种子
     */
    @Setter
    @JSONField(serialize = false)
    protected Seed seed;
    /**
     * 对应原始文本
     */
    @Setter
    @JSONField(serialize = false)
    protected String rawText;

    @Setter
    @JSONField(serialize = false)
    protected AbstractSelectable originSelectable;

    @Setter
    @Getter
    @JSONField(serialize = false)
    protected String baseUrl;


    protected boolean hasGrabSuccess() {
        return true;
    }

    protected void afterAutoFetch() {

    }

    protected void beforeAutoFetch() {

    }

    protected List<Seed> newSeeds() {
        return Collections.emptyList();
    }
}
