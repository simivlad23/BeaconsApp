package com.example.myapplication.model;

import java.util.Date;

public class BeaconRecord {

    private Date timeReacord;
    private double readRssi = -59;
    private double smootRssi = -59;
    private double meanRssi = -59;
    private double smootDistance = 1.0;
    private double meanDistance = 1.0;

    public BeaconRecord() {
    }

    public BeaconRecord(Date timeReacord, double readRssi, double smootRssi, double meanRssi, double smootDistance, double meanDistance) {
        this.timeReacord = timeReacord;
        this.readRssi = readRssi;
        this.smootRssi = smootRssi;
        this.meanRssi = meanRssi;
        this.smootDistance = smootDistance;
        this.meanDistance = meanDistance;
    }

    public Date getTimeReacord() {
        return timeReacord;
    }

    public void setTimeReacord(Date timeReacord) {
        this.timeReacord = timeReacord;
    }

    public double getReadRssi() {
        return readRssi;
    }

    public void setReadRssi(double readRssi) {
        this.readRssi = readRssi;
    }

    public double getSmootRssi() {
        return smootRssi;
    }

    public void setSmootRssi(double smootRssi) {
        this.smootRssi = smootRssi;
    }

    public double getMeanRssi() {
        return meanRssi;
    }

    public void setMeanRssi(double meanRssi) {
        this.meanRssi = meanRssi;
    }

    public double getSmootDistance() {
        return smootDistance;
    }

    public void setSmootDistance(double smootDistance) {
        this.smootDistance = smootDistance;
    }

    public double getMeanDistance() {
        return meanDistance;
    }

    public void setMeanDistance(double meanDistance) {
        this.meanDistance = meanDistance;
    }
}
