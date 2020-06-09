package com.example.myapplication.model;

public class PredictModel {

    private double beaconC7;
    private double beaconD2;
    private double beaconD1;
    private double beaconC0;

    public PredictModel() {
    }

    public PredictModel(double beaconC7, double beaconD2, double beaconD1, double beaconC0) {
        this.beaconC7 = beaconC7;
        this.beaconD2 = beaconD2;
        this.beaconD1 = beaconD1;
        this.beaconC0 = beaconC0;
    }

    public double getBeaconC7() {
        return beaconC7;
    }

    public void setBeaconC7(double beaconC7) {
        this.beaconC7 = beaconC7;
    }

    public double getBeaconD2() {
        return beaconD2;
    }

    public void setBeaconD2(double beaconD2) {
        this.beaconD2 = beaconD2;
    }

    public double getBeaconD1() {
        return beaconD1;
    }

    public void setBeaconD1(double beaconD1) {
        this.beaconD1 = beaconD1;
    }

    public double getBeaconC0() {
        return beaconC0;
    }

    public void setBeaconC0(double beaconC0) {
        this.beaconC0 = beaconC0;
    }
}
