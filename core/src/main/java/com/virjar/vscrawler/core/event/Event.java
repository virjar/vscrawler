package com.virjar.vscrawler.core.event;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by virjar on 17/4/30.
 * 
 * @author virjar
 * @since 0.0.1
 */
public class Event {
    @Getter
    private String topic;
    @Getter
    @Setter
    private int what;
    @Getter
    @Setter
    private Object data;

    /**
     * 如果事件被消费,那么不在往其他handler传递
     */
    @Getter
    private boolean handled = false;
    /**
     * 如果同步选项打开,那么事件在触发线程同步执行,而不进入事件循环
     */
    @Getter
    @Setter
    private boolean sync = false;

    public Event(String topic) {
        this.topic = topic;
    }

    public void consumed() {
        handled = true;
    }


    public void send() {
        EventLoop.getInstance().offerEvent(this);
    }
}
