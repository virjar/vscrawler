package com.virjar.vscrawler.event.systemevent;

import java.util.Collection;

import com.virjar.vscrawler.event.support.AutoEvent;
import com.virjar.vscrawler.seed.Seed;

/**
 * Created by virjar on 17/5/16.
 */
public interface NewSeedArrivalEvent {
    @AutoEvent
    void needSeed(Collection<Seed> newSeeds);
}
