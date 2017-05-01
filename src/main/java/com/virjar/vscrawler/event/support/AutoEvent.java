package com.virjar.vscrawler.event.support;

/**
 * Created by virjar on 17/4/30.
 */
public @interface AutoEvent {
    String topic();

    boolean sync() default false;
}
