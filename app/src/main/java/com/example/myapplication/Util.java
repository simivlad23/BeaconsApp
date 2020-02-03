package com.example.myapplication;

import com.example.myapplication.model.RSSI_Record;

import java.util.ArrayList;
import java.util.List;

public class Util {

    private static final double RF_A = 40; // the absolute energy which is represent by dBm at a distance of 1 meter from the transmitter
    private static final double RF_N = 2.3; // n is the signal transmission constant


    public static List<RSSI_Record> recordsList = new ArrayList<>();




    public static double getDistance(double rssi, double txPower) {
        double distance = (Math.pow(10d, -((rssi + RF_A) / 10 * RF_N))) / 10.0;

        return distance;
    }


}
