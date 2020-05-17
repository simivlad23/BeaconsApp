package com.example.myapplication.filter;

import com.example.myapplication.Beacons;
import com.example.myapplication.model.AdvertisingPacket;
import com.example.myapplication.util.AdvertistingRssiUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MeanFilter extends WindowFilter {

    public MeanFilter() {
    }

    public MeanFilter(long duration, TimeUnit timeUnit) {
        super(duration, timeUnit);
    }

    public MeanFilter(long maximumTimestamp) {
        super(maximumTimestamp);
    }

    public MeanFilter(long duration, TimeUnit timeUnit, long maximumTimestamp) {
        super(duration, timeUnit, maximumTimestamp);
    }

    @Override
    public float filter(Beacons beacon) {
        List<AdvertisingPacket> advertisingPackets = getRecentAdvertisingPackets(beacon);
        int[] rssiArray = AdvertistingRssiUtil.getRssisFromAdvertisingPackets(advertisingPackets);
        return AdvertistingRssiUtil.calculateMean(rssiArray);
    }
}
