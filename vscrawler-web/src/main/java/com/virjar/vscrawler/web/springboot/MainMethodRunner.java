package com.virjar.vscrawler.web.springboot;

import java.lang.reflect.Method;

class MainMethodRunner {
    private final String mainClassName;
    private final String[] args;

    MainMethodRunner(String mainClass, String[] args) {
        this.mainClassName = mainClass;
        this.args = args == null ? null : args.clone();
    }

    void run() throws Exception {
        Class mainClass = Thread.currentThread().getContextClassLoader().loadClass(this.mainClassName);
        Method mainMethod = mainClass.getDeclaredMethod("main", String[].class);
        mainMethod.invoke(null, this.args);
    }
}
