package com.virjar.vscrawler.resourcemanager.model;

import com.google.common.base.Preconditions;
import lombok.Getter;

/**
 * Created by virjar on 2018/1/7.<br/>资源设置
 */
@Getter
public class ResourceSetting {
    /**
     * 是否加锁,如果资源加锁,则资源在短时间不能被同时分发给不同客户端。一个资源如果能够被分发,需要满足一下条件(or关系)
     * 1. 当前没有任何客户端占用该资源
     * 2. 资源类型lock为false,证明资源无并发问题,可以在不释放资源的时候重复分发
     * 3. 该资源feedback后,资源解锁
     * 4. 该资源被分发后,客户端使用结束未执行feedback操作,资源一直被加锁,但是锁超时自动释放
     */
    private boolean lock = false;

    /**
     * 轮询队列使用smartProxyQueue模型,见:<a href="https://gitee.com/virjar/proxyipcenter/blob/master/doc/client/design/SmartProxyQueue.md">smartProxyQueue模型</a>
     * scoreFactory代表资源评分时效性,需要为正整数,其含义为分值评估历史范围,也即使用该资源历史多少次使用情况作为该资源当前的可用性分数。该值越小,代表分值变化越灵敏,具体设置多少,需要根据实际情况调参。
     */
    private int scoreFactory = 10;

    /**
     * smartProxyQueue另一个参数,用来确定队列轮询区和备用区的容量比例
     */
    private double scoreRatio = 0.3;

    /**
     * 锁强制释放时间,如果客户端得到了资源,但是并没有feedback,则该资源可能一直加锁,可能导致锁泄露问题。所以任何锁都设置超时时间,如果达到了超时时间,仍然没有释放锁,则将其强制释放,避免客户端本身问题拉跨资源分发服务,
     * 该设置只在lock flag为true的时候生效
     */
    private long lockForceLeaseDuration = 1000 * 60 * 60 * 4;

    public static ResourceSetting create() {
        return new ResourceSetting();
    }

    public ResourceSetting setLock(boolean lock) {
        this.lock = lock;
        return this;
    }

    public ResourceSetting setScoreFactory(int scoreFactory) {
        Preconditions.checkArgument(scoreFactory > 0);
        this.scoreFactory = scoreFactory;
        return this;
    }

    public ResourceSetting setScoreRatio(double scoreRatio) {
        Preconditions.checkArgument(scoreRatio > 0 && scoreRatio < 1);
        this.scoreRatio = scoreRatio;
        return this;
    }

    public ResourceSetting setLockForceLeaseDuration(long lockForceLeaseDuration) {
        Preconditions.checkArgument(lockForceLeaseDuration > 0);
        this.lockForceLeaseDuration = lockForceLeaseDuration;
        return this;
    }
}
