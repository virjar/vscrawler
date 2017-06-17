package com.virjar.vscrawler.core.seed;

import org.joda.time.DateTime;

/**
 * Created by virjar on 17/6/17.
 * 默认按天分段
 */
public class DefaultSegmentResolver implements SegmentResolver {

    @Override
    public long resolveSegmentKey(long activeTime) {
        return new DateTime(activeTime).withMillisOfDay(0).getMillis();
    }
}
