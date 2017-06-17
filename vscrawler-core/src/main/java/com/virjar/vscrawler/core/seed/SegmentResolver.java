package com.virjar.vscrawler.core.seed;

/**
 * Created by virjar on 17/6/17.<br/>
 * SegmentResolver用来实现增量抓取,SegmentResolver将种子打散到多个不同的segment中,不同segment的消重独立,存储独立,调度隔离。主要是因为消重隔离,允许同一个URL重复入库, <br/>
 * 请注意,这个不要玩太狠,最多一个小时分一次段,否则db撑不住
 */
public interface SegmentResolver {
    long resolveSegmentKey(long activeTime);
}
