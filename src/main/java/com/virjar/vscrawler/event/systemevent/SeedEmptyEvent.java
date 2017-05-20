package com.virjar.vscrawler.event.systemevent;

import com.virjar.vscrawler.event.support.AutoEvent;

/**
 * Created by virjar on 17/5/20.
 */
public interface SeedEmptyEvent {
    @AutoEvent
    void onSeedEmpty();
}
