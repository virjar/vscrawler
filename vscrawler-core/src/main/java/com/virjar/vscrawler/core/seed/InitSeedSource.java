package com.virjar.vscrawler.core.seed;

import java.util.Collection;

/**
 * Created by virjar on 17/5/15.
 */
public interface InitSeedSource {
    /**
     * vsCrawler的种子,是全部放在伯克利DB中,如果你的种子很多,可以定义默认种子源,爬虫初始化的时候会把他全部转移到本地文件
     * 
     * @return 种子数据,注意种子一旦被倒入,就不应该在保存
     */
    Collection<Seed> initSeeds();
}
