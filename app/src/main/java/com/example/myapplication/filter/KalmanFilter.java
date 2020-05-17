package com.example.myapplication.filter;

import com.example.myapplication.Beacons;
import com.example.myapplication.model.AdvertisingPacket;
import com.example.myapplication.util.AdvertistingRssiUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class KalmanFilter extends WindowFilter {

    private static float PROCESS_NOISE_DEFAULT = 0.008f;

    private float processNoise = PROCESS_NOISE_DEFAULT;

    public KalmanFilter() {
    }

    public KalmanFilter(long duration, TimeUnit timeUnit) {
        super(duration, timeUnit);
    }

    public KalmanFilter(long maximumTimestamp) {
        super(maximumTimestamp);
    }

    public KalmanFilter(long duration, TimeUnit timeUnit, long maximumTimestamp) {
        super(duration, timeUnit, maximumTimestamp);
    }

    @Override
    public float filter(Beacons beacon) {
        List<AdvertisingPacket> advertisingPackets = getRecentAdvertisingPackets(beacon);
        int[] rssiArray = AdvertistingRssiUtil.getRssisFromAdvertisingPackets(advertisingPackets);
        // Measurement noise is set to a value that relates to the noise in the actual measurements
        // (i.e. the variance of the RSSI signal).
        float measurementNoise = AdvertistingRssiUtil.calculateVariance(rssiArray);
        // used for initialization of kalman filter
        float meanRssi = AdvertistingRssiUtil.calculateMean(rssiArray);
        return calculateKalmanRssi(advertisingPackets, processNoise, measurementNoise, meanRssi);
    }

    private static float calculateKalmanRssi(List<AdvertisingPacket> advertisingPackets,
                                             float processNoise, float measurementNoise, float meanRssi) {
        float errorCovarianceRssi;
        float lastErrorCovarianceRssi = 1;
        float estimatedRssi = meanRssi;
        for (AdvertisingPacket advertisingPacket : advertisingPackets) {
            float kalmanGain = lastErrorCovarianceRssi / (lastErrorCovarianceRssi + measurementNoise);
            estimatedRssi = estimatedRssi + (kalmanGain * (advertisingPacket.getRssi() - estimatedRssi));
            errorCovarianceRssi = (1 - kalmanGain) * lastErrorCovarianceRssi;
            lastErrorCovarianceRssi = errorCovarianceRssi + processNoise;
        }
        return estimatedRssi;
    }

}
