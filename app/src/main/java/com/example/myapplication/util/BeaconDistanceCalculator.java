package com.example.myapplication.util;

public abstract class BeaconDistanceCalculator {

    public static final float PATH_LOSS_PARAMETER_OPEN_SPACE = 2;
    public static final float PATH_LOSS_PARAMETER_INDOOR = 1.7f;
    public static final float PATH_LOSS_PARAMETER_OFFICE_HARD_PARTITION = 3f;

    public static final int CALIBRATED_RSSI_AT_ONE_METER = -62;
    public static final int SIGNAL_LOSS_AT_ONE_METER = -41;

    private static float pathLossParameter = PATH_LOSS_PARAMETER_INDOOR;

    public static float calculateDistance(float rssi) {
        return calculateDistanceFormula(rssi, CALIBRATED_RSSI_AT_ONE_METER, PATH_LOSS_PARAMETER_INDOOR);
    }

    public static float calculateDistanceFormula(float rssi, float calibratedRssi, float pathLossParameter) {
        return (float) Math.pow(10, (calibratedRssi - rssi) / (10 * pathLossParameter)) *100;
    }

    public static void setPathLossParameter(float pathLossParameter) {
        BeaconDistanceCalculator.pathLossParameter = pathLossParameter;
    }

    public static float getPathLossParameter() {
        return BeaconDistanceCalculator.pathLossParameter;
    }
}