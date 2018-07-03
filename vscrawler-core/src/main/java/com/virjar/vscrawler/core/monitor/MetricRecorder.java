package com.virjar.vscrawler.core.monitor;

/**
 * Created by virjar on 2018/7/3.
 *
 * @author virjar
 * @since 0.3.1
 */
public interface MetricRecorder {
    void recordMany(String name, long count, long time, boolean saveSample);

    /**
     * 累加具体值的统计参数，可用于增加或者减少统计值
     *
     * @param name  指标名称
     * @param count 该指标记录发送的次数
     */
    void recordValue(String name, long count);

    /**
     * 覆盖统计值
     *
     * @param name 指标名称
     * @param size 记录size,也就是说记录大小数值
     */
    public void recordSize(String name, long size);
}
