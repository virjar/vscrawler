package com.virjar.vscrawler.event.support;

import java.lang.reflect.Method;
import java.util.Set;

import com.google.common.collect.Sets;
import com.virjar.vscrawler.util.ClassUtils;

/**
 * Created by virjar on 17/4/30.
 */
public class AnnotationMethodVisitor implements ClassUtils.ClassVisitor {
    private Class annotationClazz;
    private Set<Method> methodSet = Sets.newHashSet();

    public AnnotationMethodVisitor(Class annotationClazz) {
        this.annotationClazz = annotationClazz;
    }

    @Override
    public void visit(Class clazz) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getAnnotation(annotationClazz) != null) {
                methodSet.add(method);
            }
        }
    }

    public Set<Method> getMethodSet() {
        return methodSet;
    }
}
