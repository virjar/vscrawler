package com.virjar;

/**
 * Created by virjar on 2018/7/14.
 */
public class SynchronizedTest {
    public static void main(String[] args) {
        fun1();
    }

    private synchronized static void fun1() {
        System.out.println("fun1");
        fun2();
    }

    private synchronized static void fun2() {
        System.out.println("fun2");
    }
}
