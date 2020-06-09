package com.example.myapplication.model;


public class FilterRecoard {

    private long timeReacord;
    private double unfilteredRssi = -59;
    private double meanRssi = -59;
    private double kalmanRssi = -59;
    private double armaRssi = -59;
    private double realDistance = 1.0;

    public FilterRecoard() {
    }

    public FilterRecoard(double unfilteredRssi,
                         double meanRssi,
                         double kalmanRssi,
                         double armaRssi,
                         double realDistance) {

        this.timeReacord = System.currentTimeMillis();
        this.unfilteredRssi = unfilteredRssi;
        this.meanRssi = meanRssi;
        this.kalmanRssi = kalmanRssi;
        this.armaRssi = armaRssi;
        this.realDistance = realDistance;
    }

    public long getTimeReacord() {
        return timeReacord;
    }

    public void setTimeReacord(long timeReacord) {
        this.timeReacord = timeReacord;
    }

    public double getUnfilteredRssi() {
        return unfilteredRssi;
    }

    public void setUnfilteredRssi(double unfilteredRssi) {
        this.unfilteredRssi = unfilteredRssi;
    }

    public double getMeanRssi() {
        return meanRssi;
    }

    public void setMeanRssi(double meanRssi) {
        this.meanRssi = meanRssi;
    }

    public double getKalmanRssi() {
        return kalmanRssi;
    }

    public void setKalmanRssi(double kalmanRssi) {
        this.kalmanRssi = kalmanRssi;
    }

    public double getArmaRssi() {
        return armaRssi;
    }

    public void setArmaRssi(double armaRssi) {
        this.armaRssi = armaRssi;
    }

    public double getRealDistance() {
        return realDistance;
    }

    public void setRealDistance(double realDistance) {
        this.realDistance = realDistance;
    }
}