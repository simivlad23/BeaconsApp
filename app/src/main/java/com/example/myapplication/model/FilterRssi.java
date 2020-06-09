package com.example.myapplication.model;

import java.util.Date;

public class FilterRssi {

    private long timeReacord;
    private double unfilteredRssi = -59;
    private double meanRssi = -59;
    private double kalmanRssi = -59;
    private double armaRssi = -59;
    private double unfilteredRssiDist = 1.0;
    private double meanRssiDist = 1.0;
    private double kalmanRssiDist = 1.0;
    private double armaRssiDist = 1.0;
    private double unfilteredRssiDist2 = 1.0;
    private double meanRssiDist2 = 1.0;
    private double kalmanRssiDist2 = 1.0;
    private double armaRssiDist2 = 1.0;
    private double realDistance = 1.0;

    public FilterRssi() {
    }

    public FilterRssi(double unfilteredRssi,
                      double meanRssi,
                      double kalmanRssi,
                      double armaRssi,
                      double unfilteredRssiDist,
                      double meanRssiDist,
                      double kalmanRssiDist,
                      double armaRssiDist,
                      double realDistance) {

        this.timeReacord = System.currentTimeMillis();
        this.unfilteredRssi = unfilteredRssi;
        this.meanRssi = meanRssi;
        this.kalmanRssi = kalmanRssi;
        this.armaRssi=armaRssi;
        this.unfilteredRssiDist = unfilteredRssiDist;
        this.meanRssiDist = meanRssiDist;
        this.kalmanRssiDist = kalmanRssiDist;
        this.armaRssiDist= armaRssiDist;
        this.realDistance = realDistance;
    }

    public FilterRssi(double unfilteredRssi,
                      double meanRssi,
                      double kalmanRssi,
                      double armaRssi,
                      double unfilteredRssiDist,
                      double meanRssiDist,
                      double kalmanRssiDist,
                      double armaRssiDist,
                      double unfilteredRssiDist2,
                      double meanRssiDist2,
                      double kalmanRssiDist2,
                      double armaRssiDist2,
                      double realDistance) {

        this.timeReacord = System.currentTimeMillis();
        this.unfilteredRssi = unfilteredRssi;
        this.meanRssi = meanRssi;
        this.kalmanRssi = kalmanRssi;
        this.armaRssi = armaRssi;
        this.unfilteredRssiDist = unfilteredRssiDist;
        this.meanRssiDist = meanRssiDist;
        this.kalmanRssiDist = kalmanRssiDist;
        this.armaRssiDist = armaRssiDist;
        this.unfilteredRssiDist2 = unfilteredRssiDist2;
        this.meanRssiDist2 = meanRssiDist2;
        this.kalmanRssiDist2 = kalmanRssiDist2;
        this.armaRssiDist2 = armaRssiDist2;
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

    public double getUnfilteredRssiDist() {
        return unfilteredRssiDist;
    }

    public void setUnfilteredRssiDist(double unfilteredRssiDist) {
        this.unfilteredRssiDist = unfilteredRssiDist;
    }

    public double getMeanRssiDist() {
        return meanRssiDist;
    }

    public void setMeanRssiDist(double meanRssiDist) {
        this.meanRssiDist = meanRssiDist;
    }

    public double getKalmanRssiDist() {
        return kalmanRssiDist;
    }

    public void setKalmanRssiDist(double kalmanRssiDist) {
        this.kalmanRssiDist = kalmanRssiDist;
    }

    public double getRealDistance() {
        return realDistance;
    }

    public void setRealDistance(double realDistance) {
        this.realDistance = realDistance;
    }

    public double getArmaRssi() {
        return armaRssi;
    }

    public void setArmaRssi(double armaRssi) {
        this.armaRssi = armaRssi;
    }

    public double getArmaRssiDist() {
        return armaRssiDist;
    }

    public void setArmaRssiDist(double armaRssiDist) {
        this.armaRssiDist = armaRssiDist;
    }

    public double getUnfilteredRssiDist2() {
        return unfilteredRssiDist2;
    }

    public void setUnfilteredRssiDist2(double unfilteredRssiDist2) {
        this.unfilteredRssiDist2 = unfilteredRssiDist2;
    }

    public double getMeanRssiDist2() {
        return meanRssiDist2;
    }

    public void setMeanRssiDist2(double meanRssiDist2) {
        this.meanRssiDist2 = meanRssiDist2;
    }

    public double getKalmanRssiDist2() {
        return kalmanRssiDist2;
    }

    public void setKalmanRssiDist2(double kalmanRssiDist2) {
        this.kalmanRssiDist2 = kalmanRssiDist2;
    }

    public double getArmaRssiDist2() {
        return armaRssiDist2;
    }

    public void setArmaRssiDist2(double armaRssiDist2) {
        this.armaRssiDist2 = armaRssiDist2;
    }
}
