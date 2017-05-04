package com.virjar.vscrawler.event;

/**
 * Created by virjar on 17/4/30.
 * @author virjar
 * @since 0.0.1
 */
public class Event {
    private String topic;
    private int what;
    private Object data;

    /**
     * 如果事件被消费,那么不在往其他handler传递
     */
    private boolean handled = false;
    /**
     * 如果同步选项打开,那么事件在触发线程同步执行,而不进入事件循环
     */
    private boolean sync = false;

    public Event(String topic) {
        this.topic = topic;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }

    public String getTopic() {
        return topic;
    }

    public void consumed() {
        handled = true;
    }

    public boolean isHandled() {
        return handled;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public void send() {
        EventLoop.getInstance().offerEvent(this);
    }
}
