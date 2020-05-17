package com.example.myapplication.filter;


import com.example.myapplication.Beacons;
import com.example.myapplication.model.AdvertisingPacket;
import com.example.myapplication.util.AdvertistingRssiUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ArmaFilter extends WindowFilter {

    /**
     * Arma smoothing factor - the percentage of how much of the new signal will be discarded
     **/
    private static float DEFAULT_ARMA_FACTOR = 0.95f;

    private float armaRssi;

    public ArmaFilter() {
    }

    public ArmaFilter(long duration, TimeUnit timeUnit) {
        super(duration, timeUnit);
    }

    public ArmaFilter(long maximumTimestamp) {
        super(maximumTimestamp);
    }

    public ArmaFilter(long duration, TimeUnit timeUnit, long maximumTimestamp) {
        super(duration, timeUnit, maximumTimestamp);
    }

    @Override
    public float filter(Beacons beacon) {
        List<AdvertisingPacket> advertisingPackets = getRecentAdvertisingPackets(beacon);
        //use mean as initialization
        int[] rssiArray = AdvertistingRssiUtil.getRssisFromAdvertisingPackets(advertisingPackets);
        armaRssi = AdvertistingRssiUtil.calculateMean(rssiArray);
        float frequency = AdvertistingRssiUtil.getPacketFrequency(advertisingPackets.size(), duration, timeUnit);
        float armaFactor = getArmaFactor(frequency);
        for (AdvertisingPacket advertisingPacket : advertisingPackets) {
            addMeasurement(advertisingPacket.getRssi(), armaFactor);
        }
        return getFilteredRssi();
    }

    public void addMeasurement(int rssi, float armaFactor) {
        armaRssi = armaRssi - (armaFactor * (armaRssi - rssi));
    }

    public float getFilteredRssi() {
        return armaRssi;
    }

    public static float getArmaFactor(float packetFrequency) {
        //TODO make more robust to different packet frequencies
        float armaFactor = DEFAULT_ARMA_FACTOR;
        if (packetFrequency > 6) {
            armaFactor = 0.1f;
        } else if (packetFrequency > 5) {
            armaFactor = 0.25f;
        } else if (packetFrequency > 4) {
            armaFactor = 0.5f;
        }
        return armaFactor;
    }

}
