package com.virjar.vscrawler.core.seed;

import java.io.Serializable;
import java.util.Map;

import org.joda.time.DateTime;

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
    public static int STATUS_INIT = 0;
    public static int STATUS_SUCCESS = 1;
    public static int STATUS_FAILED = 2;
    public static int STATUS_RETRY = 3;
    public static int STATUS_RUNNING = 4;
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
    // 如果设置这个值,那么他放到未来的某个时间点执行,而且消重机制将会略过此类URL(在当天)
    private Long activeTimeStamp = null;

    @Getter
    @Setter
    /**
     * 分段使用,供VSCrawler使用,强烈不建议外部修改此字段,否则容易引起数据紊乱
     */
    private String segmentKey;

    @Getter
    @Setter
    private Map<String, String> ext = Maps.newHashMap();

    /**
     * 一个方便的方法,让这个种子在未来n天生效
     * 
     * @param day 天数
     */
    public void activeAfter(int day) {
        activeTimeStamp = DateTime.now().plusDays(day).getMillis();
    }

    public void retry() {
        retry++;
        if (needEnd()) {
            status = STATUS_FAILED;
        } else {
            status = STATUS_RETRY;
        }
    }

    public boolean needEnd() {
        return ignore || status == STATUS_SUCCESS || retry >= maxRetry;
    }

    /**
     * 只copy种子数据和扩展数据,其他的属于状态描述,不复制
     * 
     * @return newSeed
     */
    public Seed copy() {
        Seed seed = new Seed(data);
        seed.ext = Maps.newHashMap(ext);
        return seed;
    }

    public void setActiveTimeStamp(Long activeTimeStamp) {
        if (this.activeTimeStamp != null) {
            throw new IllegalStateException("activeTimeStamp can not update");
        }
        this.activeTimeStamp = activeTimeStamp;
    }
}
