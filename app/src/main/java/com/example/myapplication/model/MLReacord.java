package com.example.myapplication.model;

import java.util.Date;

public class MLReacord {

    private Date timeReacord;
    private double beacon1;
    private double beacon2;
    private double beacon3;
    private double beacon4;

    public MLReacord() {
    }

    public MLReacord(Date timeReacord, double beacon1, double beacon2, double beacon3, double beacon4) {
        this.timeReacord = timeReacord;
        this.beacon1 = beacon1;
        this.beacon2 = beacon2;
        this.beacon3 = beacon3;
        this.beacon4 = beacon4;
    }

    public Date getTimeReacord() {
        return timeReacord;
    }

    public void setTimeReacord(Date timeReacord) {
        this.timeReacord = timeReacord;
    }

    public double getBeacon1() {
        return beacon1;
    }

    public void setBeacon1(double beacon1) {
        this.beacon1 = beacon1;
    }

    public double getBeacon2() {
        return beacon2;
    }

    public void setBeacon2(double beacon2) {
        this.beacon2 = beacon2;
    }

    public double getBeacon3() {
        return beacon3;
    }

    public void setBeacon3(double beacon3) {
        this.beacon3 = beacon3;
    }

    public double getBeacon4() {
        return beacon4;
    }

    public void setBeacon4(double beacon4) {
        this.beacon4 = beacon4;
    }
}
