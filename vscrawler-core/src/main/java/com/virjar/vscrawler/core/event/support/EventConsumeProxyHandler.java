package com.virjar.vscrawler.core.event.support;

import com.virjar.vscrawler.core.event.Event;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by virjar on 17/5/1.
 *
 * @author virjar
 * @since 0.0.1
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
        if ("handEvent".equals(method.getName())) {// 从object继承过来的方法,不代理
            Event event = (Event) args[0];
            Map data = (Map) event.getData();
            Object[] args1 = (Object[]) data.get("args");
            return targetMethod.invoke(target, args1);
        }

        if ("hashCode".equals(method.getName())) {
            return target.hashCode() + targetMethod.hashCode();
        }

        if ("equals".equals(method.getName()) && args.length == 1) {
            Object other = args[0];
            if (other == null) {
                return false;
            }
            int theHashCode = target.hashCode() + targetMethod.hashCode();
            return theHashCode == other.hashCode();
        }

        return method.invoke(this, args);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "EventConsumeProxyHandler:" + target.toString() + "#" + targetMethod.getName();
    }
}
