package com.virjar.vscrawler.seed;

import java.io.Serializable;

import lombok.*;

/**
 * Created by virjar on 17/5/15.<br/>
 * 种子描述,vsCrawler定义中,种子没有优先级概念
 */
@RequiredArgsConstructor
@AllArgsConstructor
public class Seed implements Serializable {
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
    private int maxRetry = 3;

    public boolean needEnd() {
        return status == 1 || retry >= maxRetry;
    }

}
