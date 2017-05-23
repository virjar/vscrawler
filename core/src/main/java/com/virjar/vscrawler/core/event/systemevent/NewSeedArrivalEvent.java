package com.virjar.vscrawler.core.event.systemevent;

import java.util.Collection;

import com.virjar.vscrawler.core.seed.Seed;
import com.virjar.vscrawler.core.event.support.AutoEvent;

/**
 * Created by virjar on 17/5/16.
 */
public interface NewSeedArrivalEvent {
    @AutoEvent
    void needSeed(Collection<Seed> newSeeds);
}
