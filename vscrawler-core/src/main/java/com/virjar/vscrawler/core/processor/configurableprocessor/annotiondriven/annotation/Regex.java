package com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by virjar on 2017/12/10.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Regex {
    String value();

    int group() default 0;
}
