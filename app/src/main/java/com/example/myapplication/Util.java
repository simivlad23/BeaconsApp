package com.example.myapplication;

import android.content.Context;
import android.icu.text.Edits;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.model.Position;
import com.example.myapplication.model.RssiRecord;
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Util {

    private static final String TAG = "UTIL";

    private static final double RF_A = 23; // the absolute energy which is represent by dBm at a distance of 1 meter from the transmitter
    private static final double RF_N = 2.3; // n is the signal transmission constant
    private static final long FIVE_SECOND_MILISECOND = 10000l;
    public static List<Beacons> beaconsList = new ArrayList<>();
    private static DecimalFormat df = new DecimalFormat("#.000");
    public static List<RssiRecord> recordsList = new ArrayList<>();
    public static List<Position> positionsList = new ArrayList<>();

    public static Date startDateReading;

    public static double getDistance(double rssi, double txPower) {
        return (Math.pow(10d, -((rssi + RF_A) / 10 * RF_N))) / 10.0;
    }

    public static double getDistance2(int rssi, int txPower) {
        double distance = Math.pow(10d, ((double) txPower - rssi) / (10 * 2.3)) / 1000.0;
        return Double.parseDouble(df.format(distance));
    }

    public static void makeTaost(String message, Context context) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void initStartDateReading() {

        long timeNow = System.currentTimeMillis() + FIVE_SECOND_MILISECOND;
        startDateReading = new Date(timeNow);
        Log.i(TAG, "Time to read :" + startDateReading.toString() + " --- " + new Date(System.currentTimeMillis()).toString() + "   ; and curent time milisecond:" + startDateReading.getTime() + "  -- " + System.currentTimeMillis());

    }

    public static Date convertFromEpochToDate(long time) {

        long timeNow = System.currentTimeMillis();
        Date dateNow = new Date(timeNow);
        return dateNow;
    }

    public static void filterRecorderList() {
        //TODO to filter recorder list, every timestamp must to have value from all beacons
    }

    public static Position calcutate() {

        Beacons beacon1 = Util.beaconsList.get(0);
        Beacons beacon2 = Util.beaconsList.get(1);
        Beacons beacon3 = Util.beaconsList.get(2);

        double[][] positions = new double[][]{{beacon1.getLat(), beacon1.getLng()}, {beacon2.getLat(), beacon2.getLng()}, {beacon3.getLat(), beacon3.getLng()}};
        double[] distances = new double[]{beacon1.getDistance(), beacon2.getDistance(), beacon3.getDistance()};

        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
        LeastSquaresOptimizer.Optimum optimum = solver.solve();

// the answer
        double[] calculatedPosition = optimum.getPoint().toArray();

// error and geometry information

//        RealVector standardDeviation = optimum.getSigma(0);
//        RealMatrix covarianceMatrix = optimum.getCovariances(0);


        Log.i(TAG, "x: " + calculatedPosition[0] + " " + "y: " + calculatedPosition[1]);

        return new Position(Double.parseDouble(df.format(calculatedPosition[0])), Double.parseDouble(df.format(calculatedPosition[1])));

    }

    public static void setBeaconsPosition() {

        for (Beacons beacons : beaconsList) {
            switch (beacons.getBluetoothDevice().getAddress()) {
                case "C7:7E:A2:BD:51:4C":
                    beacons.setLat(8);
                    beacons.setLng(0);
                    break;
                case "D2:83:6A:5E:AB:F8":
                    beacons.setLat(0);
                    beacons.setLng(0);
                    break;
                case "D1:A4:D2:15:51:00":
                    beacons.setLat(0);
                    beacons.setLng(8);
                    break;
            }
        }
    }

    public static void updateBeaconsDistances() {
        for (Beacons beacons : beaconsList) {
            beacons.setAverageBleDistance();
        }
    }

    public static Position getMedianPosition() {

        double lat = 0.0;
        double lng = 0.0;

        for (Position position : positionsList) {
            lat += position.getLat();
            lng += position.getLng();
        }

        lat = lat / positionsList.size();
        lng = lng / positionsList.size();

        positionsList.clear();

        return new Position(Double.parseDouble(df.format(lat)), Double.parseDouble(df.format(lng)));

    }


}
