package com.virjar.vscrawler.event.support;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.virjar.vscrawler.event.EventHandler;
import com.virjar.vscrawler.event.EventLoop;
import com.virjar.vscrawler.util.ClassUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by virjar on 17/4/30.<br/>
 * 处理由注解自动标注的事件绑定
 * @author virjar
 * @since 0.0.1
 */
@Slf4j
public class AutoEventRegistry {
    private static AutoEventRegistry instance = null;
    private static Set<String> basePackges = Sets.newHashSet("com.virjar.vscrawler.event.systemevent");

    public synchronized static void addBasePackage(String basePackage) {
        Iterator<String> iterator = basePackges.iterator();
        while (iterator.hasNext()) {
            String oldBasePackage = iterator.next();
            if (oldBasePackage.startsWith(basePackage)) {
                iterator.remove();
                basePackges.add(basePackage);
                return;
            } else if (basePackage.startsWith(oldBasePackage)) {
                return;
            }
        }
        basePackges.add(basePackage);
    }

    private AutoEventRegistry() {
        scanDelegate();
    }

    /**
     * 懒汉式的单例模式
     * 
     * @return
     */
    public static AutoEventRegistry getInstance() {
        if (instance == null) {
            synchronized (AutoEventRegistry.class) {
                if (instance == null) {
                    instance = new AutoEventRegistry();
                }
            }
        }
        return instance;
    }

    private Map<Class, Object> allAutoEventMap = Maps.newHashMap();

    private void scanDelegate() {
        AnnotationMethodVisitor eventVisitor = new AnnotationMethodVisitor(AutoEvent.class);
        ClassUtils.scan(eventVisitor, basePackges);
        // 所有自动事件的声明
        Set<Method> eventMethodSet = eventVisitor.getMethodSet();
        Multimap<Class, Method> classMethodMultimap = toClassMap(eventMethodSet);
        // 为声明事件注册事件转化
        for (Class clazz : classMethodMultimap.keySet()) {
            delegateMethod(clazz, classMethodMultimap.get(clazz));
        }
        /*
         * AnnotationMethodVisitor eventHandlerVisitor = new AnnotationMethodVisitor(AutoEventHandler.class);
         * ClassUtils.scan(eventHandlerVisitor); Set<Method> eventHandlerMethodSet = eventHandlerVisitor.getMethodSet();
         */
    }

    /**
     * 注册事件观察者,通过注解的事件观察者
     * 
     * @param observer 观察者对象
     */
    public void registerObserver(Object observer) {
        final Class<?> observerClass = observer.getClass();

        // 这个观察者的接口类,可能有多个
        Set<Class> allSupperClass = Sets.filter(allAutoEventMap.keySet(), new Predicate<Class>() {
            @Override
            @SuppressWarnings("unchecked")
            public boolean apply(Class input) {
                return input.isAssignableFrom(observerClass);
            }
        });

        if (allSupperClass.size() == 0) {
            // TODO 自动发现父类方法,并实现监听和转化,想想不实现也好,不然使用者可能犯迷糊
            log.warn("can not registry observer:{} , observer class must implement a event interface", observer);
            return;
        }

        Method[] declaredMethods = observerClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            String topic = "";
            if (method.getAnnotation(AutoEventHandler.class) != null) {
                AutoEventHandler autoEventHandler = method.getAnnotation(AutoEventHandler.class);
                topic = autoEventHandler.topic();
            }

            // find auto Topic
            for (Class supperClazz : allSupperClass) {
                Method[] supperClazzDeclaredMethods = supperClazz.getDeclaredMethods();
                for (Method supperClazzMethod : supperClazzDeclaredMethods) {
                    if (!supperClazzMethod.getName().equals(method.getName())
                            || supperClazzMethod.getAnnotation(AutoEvent.class) == null) {
                        continue;
                    }
                    AutoEvent annotation = supperClazzMethod.getAnnotation(AutoEvent.class);
                    String eventTopic = annotation.topic();

                    if (StringUtils.isEmpty(eventTopic)) {// 父类指定过topic,那么子类必须指定topic
                        eventTopic = supperClazz.getName() + "#" + method.getName();
                    }
                    if (StringUtils.isEmpty(topic) || topic.equals(eventTopic)) {
                        // 注册一个topic为eventTopic的事件观察者
                        registerMethod(observer, method, eventTopic);
                    }
                }
            }

        }
    }

    private void registerMethod(Object obverser, Method obverserMethod, String topic) {
        EventConsumeProxyHandler eventConsumeProxyHandler = new EventConsumeProxyHandler(obverser, obverserMethod);
        EventHandler eventHandler = (EventHandler) Proxy.newProxyInstance(AutoEventRegistry.class.getClassLoader(),
                new Class[] { EventHandler.class }, eventConsumeProxyHandler);
        EventLoop.registerHandler(topic, eventHandler);
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

        EventSendProxyHandler eventSendProxyHandler = new EventSendProxyHandler();
        Object o = Proxy.newProxyInstance(AutoEventRegistry.class.getClassLoader(), new Class[] { interfaze },
                eventSendProxyHandler);
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
