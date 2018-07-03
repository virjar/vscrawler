package com.virjar.vscrawler.core.monitor;

/**
 * Created by virjar on 2018/7/3.<br>
 * vscrawler框架本身不接入任何监控平台,所以这里空实现
 *
 * @since 0.3.1
 * @author virjar
 */
class MetricIgnoreMonitor implements MetricRecorder {
    @Override
    public void recordMany(String name, long count, long time, boolean saveSample) {
        //do nothing
    }

    @Override
    public void recordValue(String name, long count) {
        //do nothing
    }

    @Override
    public void recordSize(String name, long size) {
        //do nothing
    }
}
