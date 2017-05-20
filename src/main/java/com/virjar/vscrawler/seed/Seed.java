package com.virjar.vscrawler.seed;

import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.Maps;

import lombok.*;

/**
 * Created by virjar on 17/5/15.<br/>
 * 种子描述,vsCrawler定义中,种子没有优先级概念
 */
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor // 反序列化需要
public class Seed implements Serializable {
    public static int  STATUS_INIT =0;
    public static int  STATUS_SUCCESS =0;
    public static int  STATUS_FAILED =0;
    public static int  STATUS_RETRY =0;
    // 真正的种子信息,因为需要序列化,所以直接设计为字符串
    @Getter
    @Setter
    @NonNull
    private String data;
    // 当前重试次数,如果达到了最大重试次数,那么强行终止
    @Getter
    @Setter
    private int retry = 0;

    // 0 初始化 ;1 成功 2 失败 ;3 重试中;
    @Getter
    @Setter
    private int status = 0;

    @Setter
    @Getter
    private int maxRetry = 3;

    @Getter
    @Setter
    private boolean ignore = false;

    @Getter
    @Setter
    private Map<String, String> ext = Maps.newHashMap();

    public void retry() {
        retry++;
        if(needEnd()){
            status = STATUS_FAILED;
        }else{
            status = STATUS_RETRY;
        }
    }

    public boolean needEnd() {
        return ignore || status == STATUS_SUCCESS || retry >= maxRetry;
    }

}
