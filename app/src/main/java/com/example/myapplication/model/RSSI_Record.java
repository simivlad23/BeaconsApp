package com.example.myapplication.model;

public class RSSI_Record {

    private double rssiValue;
    private double distanceCalculated;

    public RSSI_Record(double rssiValue, double distanceCalculated) {
        this.rssiValue = rssiValue;
        this.distanceCalculated = distanceCalculated;
    }

    public double getRssiValue() {
        return rssiValue;
    }

    public void setRssiValue(double rssiValue) {
        this.rssiValue = rssiValue;
    }

    public double getDistanceCalculated() {
        return distanceCalculated;
    }

    public void setDistanceCalculated(double distanceCalculated) {
        this.distanceCalculated = distanceCalculated;
    }
}
