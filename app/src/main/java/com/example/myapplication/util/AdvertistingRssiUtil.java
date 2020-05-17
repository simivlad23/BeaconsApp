package com.example.myapplication.util;

import com.example.myapplication.model.AdvertisingPacket;

import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class AdvertistingRssiUtil {

    public static int[] getRssisFromAdvertisingPackets(List<AdvertisingPacket> advertisingPackets) {
        int[] rssis = new int[advertisingPackets.size()];
        for (int i = 0; i < advertisingPackets.size(); i++) {
            rssis[i] = advertisingPackets.get(i).getRssi();
        }
        return rssis;
    }

    public static float calculateMean(int[] values) {
        int sum = 0;
        for (int i = 0; i < values.length; i++) {
            sum += values[i];
        }
        return sum / (float) values.length;
    }

    public static float calculateVariance(int[] values) {
        float mean = calculateMean(values);
        float squaredDistanceSum = 0;
        for (int i = 0; i < values.length; i++) {
            squaredDistanceSum += Math.pow(values[i] - mean, 2);
        }
        int sampleLength = Math.max(values.length - 1, 1);
        return squaredDistanceSum / sampleLength;
    }

    public static float getPacketFrequency(int packetCount, long duration, TimeUnit timeUnit) {
        if (duration == 0) {
            return 0;
        }
        return packetCount / (float) (timeUnit.toSeconds(duration));
    }


}
