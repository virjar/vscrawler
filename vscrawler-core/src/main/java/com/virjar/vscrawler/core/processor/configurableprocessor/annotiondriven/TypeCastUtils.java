package com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by virjar on 2017/12/16.<br/>处理fastjson处理不了的类型转换问题
 */
class TypeCastUtils {

    @SuppressWarnings({"unchecked", "rawtypes"})
    static <T> T cast(Object obj, Class<T> clazz) {
        if (obj == null) {
            return null;
        }

        if (clazz == null) {
            throw new IllegalArgumentException("clazz is null");
        }

        if (obj instanceof Collection) {
            Collection collection = (Collection) obj;
            if (List.class.isAssignableFrom(clazz) || clazz == ArrayList.class) {
                return (T) Lists.newArrayList(collection);
            }
            if (clazz == LinkedList.class) {
                return (T) Lists.newLinkedList(collection);
            }
        }


        return null;
    }
}
