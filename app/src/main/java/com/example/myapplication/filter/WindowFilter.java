package com.example.myapplication.filter;

import com.example.myapplication.Beacons;
import com.example.myapplication.model.AdvertisingPacket;

import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class WindowFilter implements RssiFilter {

    public static long DEFAULT_DURATION = TimeUnit.SECONDS.toMillis(5);
    public static long CONECTION_DELAY = TimeUnit.SECONDS.toMillis(5);

    protected long duration = DEFAULT_DURATION;
    protected long maximumTimestamp;
    protected long minimumTimestamp;
    protected TimeUnit timeUnit;

    public WindowFilter() {
        this(DEFAULT_DURATION, TimeUnit.MILLISECONDS, System.currentTimeMillis());
    }

    public WindowFilter(long duration, TimeUnit timeUnit) {
        this(duration, timeUnit, System.currentTimeMillis());
    }

    public WindowFilter(long maximumTimestamp) {
        this(DEFAULT_DURATION, TimeUnit.MILLISECONDS, maximumTimestamp);
    }

    public WindowFilter(long duration, TimeUnit timeUnit, long maximumTimestamp) {
        this.duration = duration;
        this.timeUnit = timeUnit;
        this.maximumTimestamp = CONECTION_DELAY + maximumTimestamp;
        this.minimumTimestamp = CONECTION_DELAY + maximumTimestamp - duration;
    }

    public void updateDuration() {
        duration = maximumTimestamp - minimumTimestamp;
    }

    public List<AdvertisingPacket> getRecentAdvertisingPackets(Beacons beacon) {
        long now = System.currentTimeMillis();
        return beacon.getAdvertisingPacketsBetween(now - duration, now);
    }

    /*
        Getter & Setter
     */

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public long getDuration() {
        return duration;
    }

    public long getMaximumTimestamp() {
        return maximumTimestamp;
    }

    public void setMaximumTimestamp(long maximumTimestamp) {
        this.maximumTimestamp = maximumTimestamp;
        updateDuration();
    }

    public long getMinimumTimestamp() {
        return minimumTimestamp;
    }

    public void setMinimumTimestamp(long minimumTimestamp) {
        this.minimumTimestamp = minimumTimestamp;
        updateDuration();
    }


}
