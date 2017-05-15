package com.virjar.vscrawler.event.support;

import java.lang.reflect.Method;
import java.util.Set;

import com.google.common.collect.Sets;
import com.virjar.vscrawler.util.ClassScanner;

/**
 * Created by virjar on 17/4/30.
 * @author virjar
 * @since 0.0.1
 */
public class AnnotationMethodVisitor implements ClassScanner.ClassVisitor {
    private Class annotationClazz;
    private Set<Method> methodSet = Sets.newHashSet();

    public AnnotationMethodVisitor(Class annotationClazz) {
        this.annotationClazz = annotationClazz;
    }

    @Override
    public void visit(Class clazz) {
        try {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getAnnotation(annotationClazz) != null) {
                    methodSet.add(method);
                }
            }
        } catch (Throwable e) {
            // do nothing 可能有classNotFoundException
        }
    }

    public Set<Method> getMethodSet() {
        return methodSet;
    }
}
