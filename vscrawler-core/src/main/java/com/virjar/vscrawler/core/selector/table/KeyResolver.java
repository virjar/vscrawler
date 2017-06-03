package com.virjar.vscrawler.core.selector.table;

/**
 *
 * 将table中的column字段 自动转换成需要目标key存储
 * 与valueResolver配合  实现key对应db的column的key对照入库
 * Created by mario1oreo on 2017/6/3.
 */
public interface KeyResolver {

    String keyParser(String pageKey);
}
