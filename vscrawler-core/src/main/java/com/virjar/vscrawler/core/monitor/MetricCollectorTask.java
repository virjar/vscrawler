package com.virjar.vscrawler.core.monitor;

import lombok.extern.slf4j.Slf4j;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by virjar on 2018/7/3.<br>
 * 统一的异步打点,防止每个业务模块自己收集导致线程资源浪费
 */
@Slf4j
public class MetricCollectorTask {

    private static final Timer timer;

    static {
        timer = new Timer("MetricCollectorTask", true);
        //每10s一次采样
        timer.schedule(new MonitorTask(), 0, 10000);
    }

    private static class MonitorTask extends TimerTask {
        @Override
        public void run() {
            for (WeakReference<MetricCollector> reference : metricIgnoreMonitors) {
                MetricCollector metricCollector = reference.get();
                if (metricCollector == null) {
                    continue;
                }
                try {
                    metricCollector.doCollect();
                } catch (Exception e) {
                    log.error("failed to execute metric collect task", e);
                }
            }
        }
    }

    public interface MetricCollector {
        void doCollect();
    }

    private static CopyOnWriteArraySet<WeakReference<MetricCollector>> metricIgnoreMonitors = new CopyOnWriteArraySet<>();

    public static void register(MetricCollector metricCollector) {
        metricIgnoreMonitors.add(new WeakReference<>(metricCollector));
    }

    public static void unRegister(MetricCollector metricCollector) {
        for (WeakReference<MetricCollector> reference : metricIgnoreMonitors) {
            MetricCollector metricCollector1 = reference.get();
            if (metricCollector1 == null) {
                continue;
            }
            if (metricCollector1.equals(metricCollector)) {
                metricIgnoreMonitors.remove(reference);
            }
        }
    }
}
