package com.virjar.vscrawler.core.config;

import java.util.List;

/**
 * 资源加载接口
 * 
 * @author 杨尚川
 */
public interface ResourceLoader {
    /**
     * 清空数据
     */
    void clear();

    /**
     * 初始加载全部数据
     * 
     * @param lines 初始全部数据
     */
    void load(List<String> lines);

    /**
     * 动态增加一行数据
     * 
     * @param line 动态新增数据（一行）
     */
    void add(String line);

    /**
     * 动态移除一行数据
     * 
     * @param line 动态移除数据（一行）
     */
    void remove(String line);
}