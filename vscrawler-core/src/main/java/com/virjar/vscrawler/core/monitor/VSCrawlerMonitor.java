package com.virjar.vscrawler.core.monitor;

/**
 * Created by virjar on 2018/7/3.<br>
 * 监控指标抽象层,可以由这个类适配各自公司的指标监控平台
 *
 * @author virjar
 * @since 0.3.1
 */
public class VSCrawlerMonitor {
    private static MetricRecorder metricRecorder = new MetricIgnoreMonitor();

    public static void replaceCrawlerMonitorComponent(MetricRecorder metricRecorder) {
        VSCrawlerMonitor.metricRecorder = metricRecorder;
    }

    public static void recordMany(String name, long count, long time, boolean saveSample) {
        metricRecorder.recordMany(name, count, time, saveSample);
    }

    /**
     * 记录一条统计，统计数加1，时间累加。统计窗口是一分钟，最终监控反映的是时间的平均值。
     *
     * @param name 指标名称
     * @param time 该记录的时间
     */
    public static void recordOne(String name, long time) {
        recordMany(name, 1, time, false);
    }

    /**
     * 记录一条统计，统计数加1，时间累加。统计窗口是一分钟。
     *
     * @param name 指标名称
     */
    public static void recordOne(String name) {
        recordMany(name, 1, 0, false);
    }

    /**
     * 在原来recordOne的基础上，增加了对样本的采集，用于统计得到监控指标的分位数，比如 P50/90/95/99。
     * 采样算法是t-digest。
     * 注意：需要修改原有的qmonitor.jsp，QMonitor.getValues()返回的是Map<String, Object>，不是原来的Map<String, Long>
     */
    public static void recordQuantile(String name, long time) {
        recordMany(name, 1, time, true);
    }

    public static void decrRecord(String name) {
        recordMany(name, -1, 0, false);
    }

    /**
     * 可以一次记录多个统计次数和时间
     *
     * @param name  指标名称
     * @param count 指标对应的发生次数
     * @param time  该记录最终耗时
     */
    public static void recordMany(String name, long count, long time) {
        recordMany(name, count, time, false);
    }

    /**
     * 累加具体值的统计参数，可用于增加或者减少统计值
     *
     * @param name
     * @param count
     * @return MonitorItem
     */
    public static void recordValue(String name, long count) {
        metricRecorder.recordValue(name, count);
    }

    /**
     * 覆盖统计值
     *
     * @param name
     * @param size
     * @return
     */
    public static void recordSize(String name, long size) {
        metricRecorder.recordSize(name, size);
    }


}
