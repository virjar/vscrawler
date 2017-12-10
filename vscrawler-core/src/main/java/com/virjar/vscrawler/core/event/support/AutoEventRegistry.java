package com.virjar.vscrawler.core.event.support;

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.virjar.vscrawler.core.VSCrawlerContext;
import com.virjar.vscrawler.core.event.EventHandler;
import com.virjar.vscrawler.core.util.ClassScanner;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by virjar on 17/4/30.<br/>
 * 处理由注解自动标注的事件绑定
 *
 * @author virjar
 * @since 0.0.1
 */
@Slf4j
public class AutoEventRegistry {
    @NonNull
    private VSCrawlerContext vsCrawlerContext;
    private static Set<String> basePackages = Sets.newHashSet("com.virjar.vscrawler.core.event.systemevent");

    public synchronized static void addBasePackage(String basePackage) {
        Iterator<String> iterator = basePackages.iterator();
        while (iterator.hasNext()) {
            String oldBasePackage = iterator.next();
            if (oldBasePackage.startsWith(basePackage)) {
                iterator.remove();
                basePackages.add(basePackage);
                return;
            } else if (basePackage.startsWith(oldBasePackage)) {
                return;
            }
        }
        basePackages.add(basePackage);
    }

    public AutoEventRegistry(VSCrawlerContext vsCrawlerContext) {
        this.vsCrawlerContext = vsCrawlerContext;
        scanDelegate();
    }

    private Map<Class, Object> allAutoEventMap = Maps.newHashMap();

    private void scanDelegate() {
        AnnotationMethodVisitor eventVisitor = new AnnotationMethodVisitor(AutoEvent.class);
        ClassScanner.scan(eventVisitor, basePackages);
        // 所有自动事件的声明
        registerMethods(eventVisitor.getMethodSet());
    }

    public void registerEvent(Class clazz) {
        if (!clazz.isInterface()) {
            throw new IllegalStateException("" + clazz + " is not a interface");
        }
        AnnotationMethodVisitor eventVisitor = new AnnotationMethodVisitor(AutoEvent.class);
        eventVisitor.visit(clazz);
        registerMethods(eventVisitor.getMethodSet());
    }

    private void registerMethods(Set<Method> methods) {
        Multimap<Class, Method> classMethodMultimap = toClassMap(methods);
        // 为声明事件注册事件转化
        for (Class clazz : classMethodMultimap.keySet()) {
            delegateMethod(clazz, classMethodMultimap.get(clazz));
        }
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

    private void registerMethod(Object observer, Method observerMethod, String topic) {
        EventConsumeProxyHandler eventConsumeProxyHandler = new EventConsumeProxyHandler(observer, observerMethod);
        EventHandler eventHandler = (EventHandler) Proxy.newProxyInstance(AutoEventRegistry.class.getClassLoader(),
                new Class[]{EventHandler.class}, eventConsumeProxyHandler);
        vsCrawlerContext.getEventLoop().registerHandler(topic, eventHandler);
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

        EventSendProxyHandler eventSendProxyHandler = new EventSendProxyHandler(vsCrawlerContext);
        Object o = Proxy.newProxyInstance(AutoEventRegistry.class.getClassLoader(), new Class[]{interfaze},
                eventSendProxyHandler);
        allAutoEventMap.put(interfaze, o);
    }

    public <T> DelayEventSender<T> createDelayEventSender(Class<T> interfaze, long afterTimeMillis) {
        return new DelayEventSender<>(interfaze, afterTimeMillis, vsCrawlerContext);
    }

    public <T> DelayEventSender<T> createDelayEventSender(Class<T> interfaze) {
        return createDelayEventSender(interfaze, 0);
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
