package com.virjar.vscrawler.core.serialize;

import com.virjar.vscrawler.core.processor.GrabResult;
import com.virjar.vscrawler.core.seed.Seed;

/**
 * Created by virjar on 17/4/16.
 *
 * @author virjar
 * @since 0.0.1
 */
public interface Pipeline {
    void saveItem(GrabResult grabResult, Seed seed);
}
