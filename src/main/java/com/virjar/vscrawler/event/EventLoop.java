package com.virjar.vscrawler.event;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by virjar on 17/4/30.
 * @author virjar
 * @since 0.0.1
 */
@Slf4j
public class EventLoop {
    private static EventLoop instance = new EventLoop();
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    public static EventLoop getInstance() {
        return instance;
    }

    private ConcurrentMap<String, Set<EventHandler>> allHandlers = Maps.newConcurrentMap();

    private EventLoop() {
    }

    private LinkedBlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>();

    public void offerEvent(Event event) {
        /*
         * Preconditions.checkArgument( allHandlers.containsKey(event.getTopic()) &&
         * allHandlers.get(event.getTopic()).size() > 0, "cannot find handle for event:{}", event.getTopic());
         */
        if (!allHandlers.containsKey(event.getTopic()) || allHandlers.get(event.getTopic()).size() < 0) {
            log.warn("cannot find handle for event:{}", event.getTopic());
            return;
        }
        if (event.isSync()) {
            disPatch(event);
        } else {
            try {
                eventQueue.put(event);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized static void registerHandler(String topic, EventHandler eventHandler) {
        ConcurrentMap<String, Set<EventHandler>> allHandlers = instance.allHandlers;
        Set<EventHandler> eventHandlers = allHandlers.get(topic);
        if (eventHandlers == null) {
            eventHandlers = Sets.newHashSet();
            allHandlers.put(topic, eventHandlers);
        }
        eventHandlers.add(eventHandler);

    }

    public void loop() {
        if (isRunning.compareAndSet(false, true)) {
            Thread thread = new Thread("vsCrawlerEventLoop") {
                @Override
                public void run() {
                    while (isRunning.get()) {
                        try {
                            Event poll = eventQueue.take();
                            disPatch(poll);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            };
            thread.setDaemon(true);
            thread.start();
        }
    }

    public void disPatch(Event event) {
        String topic = event.getTopic();
        for (EventHandler eventHandler : allHandlers.get(topic)) {
            try {
                eventHandler.handEvent(event);
                if (event.isHandled()) {
                    break;
                }
            } catch (Exception e) {
                log.error("error when hand event:{}", topic, e);
            }
        }

    }
}
