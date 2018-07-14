package com.virjar;

import com.google.common.collect.Lists;
import com.googlecode.aviator.AviatorEvaluator;

import java.util.List;

/**
 * Created by virjar on 17/5/13.
 */
public class Test {
    public static void main(String[] args) {
        System.out.println(Long.MAX_VALUE < Double.MAX_VALUE);
    }

    public static void main1(String[] args) {
        List<String> symble = Lists.newArrayList("+", "-", "*", "/", "%", "^");

        for (String s1 : symble) {
            for (String s2 : symble) {
                String expression = "4" + s1 + "4" + s2 + "4";
                Object exec = AviatorEvaluator.exec(expression);
                System.out.println(expression + "=" + exec);
            }
        }
    }
}
