package com.example.myapplication.model;

import java.util.Date;

public class RssiRecord {

    private String deviceName;
    private String deviceAddress;
    private double rssiValue;
    private double distanceCalculated;
    private Date timeReacord;

    public RssiRecord() {

    }

    public RssiRecord(String deviceName, String deviceAddress, double rssiValue, double distanceCalculated, Date date) {
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
        this.rssiValue = rssiValue;
        this.distanceCalculated = distanceCalculated;
        this.timeReacord = date;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
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

    public Date getTimeReacord() {
        return timeReacord;
    }

    public void setTimeReacord(Date timeReacord) {
        this.timeReacord = timeReacord;
    }
}
