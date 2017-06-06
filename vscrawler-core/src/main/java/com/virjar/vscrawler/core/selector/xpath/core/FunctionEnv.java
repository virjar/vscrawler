package com.virjar.vscrawler.core.selector.xpath.core;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.virjar.dungproxy.client.ippool.config.ObjectFactory;
import com.virjar.vscrawler.core.selector.xpath.core.function.NameAware;
import com.virjar.vscrawler.core.selector.xpath.core.function.axis.AxisFunction;
import com.virjar.vscrawler.core.selector.xpath.core.function.filter.FilterFunction;
import com.virjar.vscrawler.core.selector.xpath.core.function.select.SelectFunction;
import com.virjar.vscrawler.core.util.ClassScanner;

import lombok.extern.slf4j.Slf4j;

/**
 * 函数环境,包括抽取函数,过滤函数,轴函数三类,会注册默认的函数,如果你需要扩展自己的<br/>
 * 可以在 package:com.virjar.vscrawler.core.selector.xpath.core.function 下面增加自己的对应函数,也可以通过api注册
 */
@Slf4j
public class FunctionEnv {
    private static Map<String, SelectFunction> selectFunctions = Maps.newHashMap();
    private static Map<String, FilterFunction> filterFunctions = Maps.newHashMap();
    private static Map<String, AxisFunction> axisFunctions = Maps.newHashMap();

    static {
        registerDefault();
    }

    private static void registerDefault() {
        ClassScanner.SubClassVisitor<NameAware> functionVisitor = new ClassScanner.SubClassVisitor<NameAware>(true,
                NameAware.class);
        ClassScanner.scan(functionVisitor,
                Lists.newArrayList("com.virjar.vscrawler.core.selector.xpath.core.function"));
        // 系统所有的类
        List<Class<? extends NameAware>> allFunctionClasses = functionVisitor.getSubClass();
        for (Class<? extends NameAware> clazz : allFunctionClasses) {
            NameAware nameAware = ObjectFactory.newInstance(clazz);
            if (nameAware instanceof SelectFunction) {
                registSelectFunction((SelectFunction) nameAware);
            } else if (nameAware instanceof FilterFunction) {
                registFilterFunction((FilterFunction) nameAware);
            } else if (nameAware instanceof AxisFunction) {
                registAxisFunciton((AxisFunction) nameAware);
            } else {
                log.warn("函数:{}: {} 不是合法的函数,不能注册到xpath中", nameAware.getName(), nameAware.getClass().getName());
            }
        }
    }

    public synchronized static void registSelectFunction(SelectFunction selectFunction) {
        if (selectFunctions.containsKey(selectFunction.getName())) {
            log.warn("抽取函数:{}已经存在,旧函数将会被替代", selectFunction.getName());
        }
        selectFunctions.put(selectFunction.getName(), selectFunction);
    }

    public synchronized static void registFilterFunction(FilterFunction filterFunction) {
        if (filterFunctions.containsKey(filterFunction.getName())) {
            log.warn("过滤函数:{}已经存在,旧函数将会被替代", filterFunction.getName());
        }
        filterFunctions.put(filterFunction.getName(), filterFunction);
    }

    public synchronized static void registAxisFunciton(AxisFunction axisFunction) {
        if (axisFunctions.containsKey(axisFunction.getName())) {
            log.warn("轴函数:{}已经存在,旧函数将会被替代", axisFunction.getName());
        }
        axisFunctions.put(axisFunction.getName(), axisFunction);
    }

    public static SelectFunction getSelectFunction(String functionName) {
        return selectFunctions.get(functionName);
    }

    public static FilterFunction getFilterFunction(String functionName) {
        return filterFunctions.get(functionName);
    }

    public static AxisFunction getAxisFunction(String functionName) {
        return axisFunctions.get(functionName);
    }
}
