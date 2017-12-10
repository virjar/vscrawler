package com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Field;

/**
 * Created by virjar on 2017/12/10.
 */
@AllArgsConstructor
public class FetchTask {
    @Getter
    private Field field;
    @Getter
    private ModelSelector modelSelector;

}
