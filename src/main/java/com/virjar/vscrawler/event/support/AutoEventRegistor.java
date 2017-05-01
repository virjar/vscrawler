package com.virjar.vscrawler.event.support;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.virjar.vscrawler.util.ClassUtils;

/**
 * Created by virjar on 17/4/30.<br/>
 * 处理由注解自动标注的事件绑定
 */
public class AutoEventRegistor {
    private AutoEventRegistor instance = new AutoEventRegistor();

    private AutoEventRegistor() {
        scanAndRegist();
    }

    public AutoEventRegistor getInstance() {
        return instance;
    }

    private Map<Class, Object> allAutoEventMap = Maps.newHashMap();

    private void scanAndRegist() {
        AnnotationMethodVisitor eventVisitor = new AnnotationMethodVisitor(AutoEvent.class);
        ClassUtils.scan(eventVisitor);
        // 所有自动事件的声明
        Set<Method> eventMethodSet = eventVisitor.getMethodSet();
        Multimap<Class, Method> classMethodMultimap = toClassMap(eventMethodSet);
        // 为声明事件注册事件转化
        for (Class clazz : classMethodMultimap.keySet()) {
            delegateMethod(clazz, classMethodMultimap.get(clazz));
        }

        AnnotationMethodVisitor eventHandlerVisitor = new AnnotationMethodVisitor(AutoEventHandler.class);
        ClassUtils.scan(eventHandlerVisitor);
        Set<Method> eventHandlerMethodSet = eventHandlerVisitor.getMethodSet();

    }

    private void delegateMethod(Class interfaze, Collection<Method> methods) {
        if (!interfaze.isInterface()) {
            throw new IllegalStateException("can not make " + interfaze.getName() + " to be a event");
        }
        // check methods
        Set<String> methodNameSet = Sets.newHashSet();
        for (Method method : methods) {
            if (method.getReturnType() != Void.TYPE) {
                throw new IllegalStateException("自动事件方法返回值必须为void" + method.getName());
            }
            if (methodNameSet.contains(method.getName())) {
                throw new IllegalStateException("自动事件不允许多个重名方法存在" + method.getName());
            }
            methodNameSet.add(method.getName());
        }

        EventProxyHandler eventProxyHandler = new EventProxyHandler();
        Object o = Proxy.newProxyInstance(AutoEventRegistor.class.getClassLoader(), new Class[] { interfaze },
                eventProxyHandler);
        allAutoEventMap.put(interfaze, o);
    }

    @SuppressWarnings("unchecked")
    public <T> T findEventDeclaring(Class<T> interfaze) {
        return (T) allAutoEventMap.get(interfaze);
    }

    private Multimap<Class, Method> toClassMap(Set<Method> methods) {
        Multimap<Class, Method> ret = ArrayListMultimap.create();
        for (Method method : methods) {
            ret.put(method.getDeclaringClass(), method);
        }
        return ret;
    }

}
