package com.virjar.vscrawler.core.selector.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by virjar on 17/6/30.
 */
public abstract class AbstractSelectable<T> {

    @Getter
    private String rowText;
    @Getter
    private String baseUrl;

    public AbstractSelectable(String rowText) {
        this(null, rowText);
    }

    public AbstractSelectable(String baseUrl, String rowText) {
        this.baseUrl = baseUrl;
        this.rowText = rowText;
    }

    @Setter
    protected T model;

    public abstract T createOrGetModel();

}
