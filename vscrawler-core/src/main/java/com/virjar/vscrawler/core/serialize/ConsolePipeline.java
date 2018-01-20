package com.virjar.vscrawler.core.serialize;

import com.virjar.vscrawler.core.processor.GrabResult;
import com.virjar.vscrawler.core.seed.Seed;

/**
 * Created by virjar on 17/4/16.
 *
 * @author virjar
 * @since 0.0.1
 */
public class ConsolePipeline implements Pipeline {
    @Override
    public void saveItem(GrabResult grabResult, Seed seed) {
        for (String str : grabResult.allResult()) {
            System.out.println(str);
        }
    }
}
