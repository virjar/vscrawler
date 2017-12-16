package com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by virjar on 2017/12/10.
 * *
 *
 * @author virjar
 * @since 0.2.1
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Xpath {
    String value();

    //所有抽取注解都应该有这个元素,用来处理集合元素泛型擦除问题
    Class elementType() default Object.class;
}
