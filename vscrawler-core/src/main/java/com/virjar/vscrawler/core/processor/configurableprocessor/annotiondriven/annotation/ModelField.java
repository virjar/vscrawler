package com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by virjar on 2017/12/10.<br/>
 * 标记这个字段需要使用一个子模型来进行解析
 * *
 *
 * @author virjar
 * @since 0.2.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ModelField {
}
