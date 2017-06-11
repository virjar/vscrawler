package com.virjar.vscrawler.core.selector.xpath.core.parse.expression;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.virjar.dungproxy.client.ippool.config.ObjectFactory;
import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.node.AlgorithmUnit;
import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.operator.OpKey;
import com.virjar.vscrawler.core.util.ClassScanner;

import lombok.Getter;

/**
 * Created by virjar on 17/6/10.
 */
public class OperatorEnv {
    private static TreeSet<AlgorithmHolder> allOperators = Sets.newTreeSet();
    private static HashMap<String, AlgorithmHolder> operatorMaps = Maps.newHashMap();

    static {
        registerDefault();
    }

    @SuppressWarnings("unchecked")
    private static void registerDefault() {
        ClassScanner.AnnotationClassVisitor visitor = new ClassScanner.AnnotationClassVisitor(OpKey.class);
        ClassScanner.scan(visitor,
                Lists.newArrayList("com.virjar.vscrawler.core.selector.xpath.core.parse.expression.operator"));
        Set<Class> classSet = visitor.getClassSet();
        for (Class clazz : classSet) {
            if (AlgorithmUnit.class.isAssignableFrom(clazz)) {
                addOperator(clazz);
            }
        }
    }

    public static void addOperator(String key, int priority, Class<? extends AlgorithmUnit> algorithmUnit) {
        Preconditions.checkArgument(!StringUtils.equals(key, "#"),
                "\"#\" can not to be register a operator,please contact author virjar@virjar.com");
        AlgorithmHolder holder = new AlgorithmHolder(algorithmUnit, key, priority);
        allOperators.add(holder);
        operatorMaps.put(key, holder);
    }

    public static void addOperator(Class<? extends AlgorithmUnit> algorithmUnitClass) {
        OpKey annotation = algorithmUnitClass.getAnnotation(OpKey.class);
        if (annotation == null) {
            throw new IllegalStateException("can not register operator for class " + algorithmUnitClass
                    + ",such can not resolve operator name");
        }
        String key = annotation.value();
        if (StringUtils.isBlank(key)) {
            throw new IllegalStateException(
                    "can not register operator for class " + algorithmUnitClass + " ,such operator name is empty");
        }
        addOperator(key, annotation.priority(), algorithmUnitClass);
    }

    public static AlgorithmUnit createByName(String operatorName) {
        AlgorithmHolder holder = operatorMaps.get(operatorName);
        return ObjectFactory.newInstance(holder.aClass);
    }

    public static int judgePriority(String operatorName) {
        if (StringUtils.equals(operatorName, "#")) {
            // 这是一个特殊逻辑,运算栈需要有一个兜底判断的操作符,所以#是RPN默认的一个运算符,但是他不会表现在编译好的语法树里面
            return Integer.MIN_VALUE;
        }
        AlgorithmHolder holder = operatorMaps.get(operatorName);
        return holder.priority;
    }

    public static synchronized List<AlgorithmHolder> allAlgorithmUnitList() {
        return Lists.newCopyOnWriteArrayList(allOperators);
    }

    public static class AlgorithmHolder implements Comparable<AlgorithmHolder> {
        @Getter
        private String key;
        @Getter
        private Class<? extends AlgorithmUnit> aClass;

        private int priority;

        public AlgorithmHolder(Class<? extends AlgorithmUnit> aClass, String key, int priority) {
            this.aClass = aClass;
            this.key = key;
            this.priority = priority;
        }

        @Override
        public int compareTo(AlgorithmHolder o) {
            String thisKey = key;
            String otherKey = o.key;
            if (thisKey.length() != otherKey.length()) {
                return otherKey.length() - thisKey.length();
            }
            return thisKey.compareTo(otherKey);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            AlgorithmHolder that = (AlgorithmHolder) o;

            return key != null ? key.equals(that.key) : that.key == null;

        }

        @Override
        public int hashCode() {
            return key != null ? key.hashCode() : 0;
        }
    }
}
