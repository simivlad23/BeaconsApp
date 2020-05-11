package com.example.myapplication.model;

import java.util.Date;

public class MLReacord {

    private Date timeReacord;
    private double beaconC7;
    private double beaconD2;
    private double beaconD1;
    private double beaconC0;
    private double x;
    private double y;

    public MLReacord() {
    }

    public MLReacord(Date timeReacord, double beaconC7, double beaconD2, double beaconD1, double beaconC0, double x, double y) {
        this.timeReacord = timeReacord;
        this.beaconC7 = beaconC7;
        this.beaconD2 = beaconD2;
        this.beaconD1 = beaconD1;
        this.beaconC0 = beaconC0;
        this.x = x;
        this.y = y;
    }

    public Date getTimeReacord() {
        return timeReacord;
    }

    public void setTimeReacord(Date timeReacord) {
        this.timeReacord = timeReacord;
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

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
