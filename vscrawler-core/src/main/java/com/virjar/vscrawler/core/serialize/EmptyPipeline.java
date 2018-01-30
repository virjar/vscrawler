package com.virjar.vscrawler.core.serialize;

import com.virjar.vscrawler.core.processor.GrabResult;
import com.virjar.vscrawler.core.seed.Seed;

/**
 * Created by virjar on 2018/1/30.<br>
 * 空pipline,在线抓取,pipline无意义,使用空pipline替代
 *
 * @since 0.2.7
 */
public class EmptyPipeline implements Pipeline {
    public static EmptyPipeline instance = new EmptyPipeline();

    @Override
    public void saveItem(GrabResult grabResult, Seed seed) {
        //do nothing
    }
}
