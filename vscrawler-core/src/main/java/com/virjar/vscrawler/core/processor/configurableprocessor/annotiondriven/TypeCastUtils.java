package com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.TypeUtils;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.BooleanUtils;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by virjar on 2017/12/16.<br/>处理fastjson处理不了的类型转换问题
 */
class TypeCastUtils {

    @SuppressWarnings({"unchecked", "rawtypes"})
    static <T> T cast(Object obj, Class<T> clazz, final Class helpClazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz is null");
        }

        if (Boolean.class.isAssignableFrom(clazz) || boolean.class.isAssignableFrom(clazz)) {
            //转化为boolean
            if (obj instanceof Boolean) {
                return (T) obj;
            }
            if (obj instanceof String) {
                return (T) (Boolean) BooleanUtils.toBoolean(obj.toString());
            }
            if (obj instanceof Collection) {
                return (T) (Boolean) (((Collection) obj).size() > 0);
            }
            return (T) (Boolean) (obj != null);
        }

        if (obj == null) {
            return null;
        }


        if (obj instanceof Collection) {
            Collection collection = (Collection) obj;
            if (helpClazz != Object.class && List.class.isAssignableFrom(clazz) || clazz == ArrayList.class || clazz == LinkedList.class) {
                collection = Collections2.transform(collection, new Function() {
                    @Override
                    public Object apply(Object input) {
                        if (input instanceof Element) {
                            return ((Element) input).ownText();
                        }
                        return TypeUtils.cast(input, helpClazz, ParserConfig.getGlobalInstance());
                    }
                });
            }
            if (List.class.isAssignableFrom(clazz) || clazz == ArrayList.class) {
                return (T) Lists.newArrayList(collection);
            }
            if (clazz == LinkedList.class) {
                return (T) Lists.newLinkedList(collection);
            }
        }


        if (obj instanceof Element && !Element.class.isAssignableFrom(clazz)) {
            return (T) ((Element) obj).ownText();
        }


        return null;
    }
}
