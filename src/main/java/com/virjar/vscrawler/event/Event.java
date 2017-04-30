package com.virjar.vscrawler.event;

/**
 * Created by virjar on 17/4/30.
 */
public class Event {
    private String topic;
    private int what;
    private Object data;

    private boolean handled = false;

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

    public void consumed(){
        handled = true;
    }

    public boolean isHandled() {
        return handled;
    }

    public void send() {
        EventLoop.getInstance().offerEvent(this);
    }
}
