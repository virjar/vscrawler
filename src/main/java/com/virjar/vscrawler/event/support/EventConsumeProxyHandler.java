package com.virjar.vscrawler.event.support;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import com.virjar.vscrawler.event.Event;

/**
 * Created by virjar on 17/5/1.
 */
public class EventConsumeProxyHandler implements InvocationHandler {
    private Object target;
    private Method targetMethod;

    public EventConsumeProxyHandler(Object target, Method targetMethod) {
        this.target = target;
        this.targetMethod = targetMethod;
        targetMethod.setAccessible(true);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Event event = (Event) args[0];
        Map data = (Map) event.getData();
        Object[] args1 = (Object[]) data.get("args");
        return targetMethod.invoke(target, args1);
    }
}
