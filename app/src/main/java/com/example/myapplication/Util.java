package com.example.myapplication;

import android.content.Context;
import android.icu.text.Edits;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.model.Position;
import com.example.myapplication.model.RssiRecord;

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
//
//    public static Position calculateActualPosition() {
//
//
//        double[] P1 = new double[2];
//        double[] P2 = new double[2];
//        double[] P3 = new double[2];
//        double[] ex = new double[2];
//        double[] ey = new double[2];
//        double[] p3p1 = new double[2];
//        double jval = 0.0;
//        double temp = 0.0;
//        double ival = 0.0;
//        double p3p1i = 0.0;
//        double triptx;
//        double tripty;
//        double xval;
//        double yval;
//        double t1;
//        double t2;
//        double t3;
//        double t;
//        double exx;
//        double d;
//        double eyy;
//
//        //TRANSALTE POINTS TO VECTORS
//        //POINT 1
//        P1[0] = Util.beaconsList.get(0).getLat();
//        P1[1] = Util.beaconsList.get(0).getLng();
//        //POINT 2
//        P2[0] = Util.beaconsList.get(1).getLat();
//        P2[1] = Util.beaconsList.get(1).getLng();
//        //POINT 3
//        P3[0] = Util.beaconsList.get(2).getLat();
//        P3[1] = Util.beaconsList.get(2).getLng();
//
//        //TRANSFORM THE METERS VALUE FOR THE MAP UNIT
//        //DISTANCE BETWEEN POINT 1 AND MY LOCATION
//        double distance1 = Util.beaconsList.get(0).getDistance();
//        //DISTANCE BETWEEN POINT 2 AND MY LOCATION
//        double distance2 = Util.beaconsList.get(1).getDistance();
//        //DISTANCE BETWEEN POINT 3 AND MY LOCATION
//        double distance3 = Util.beaconsList.get(2).getDistance();
//
//        for (int i = 0; i < P1.length; i++) {
//            t1 = P2[i];
//            t2 = P1[i];
//            t = t1 - t2;
//            temp += (t * t);
//        }
//        d = Math.sqrt(temp);
//        for (int i = 0; i < P1.length; i++) {
//            t1 = P2[i];
//            t2 = P1[i];
//            exx = (t1 - t2) / (Math.sqrt(temp));
//            ex[i] = exx;
//        }
//        for (int i = 0; i < P3.length; i++) {
//            t1 = P3[i];
//            t2 = P1[i];
//            t3 = t1 - t2;
//            p3p1[i] = t3;
//        }
//        for (int i = 0; i < ex.length; i++) {
//            t1 = ex[i];
//            t2 = p3p1[i];
//            ival += (t1 * t2);
//        }
//        for (int i = 0; i < P3.length; i++) {
//            t1 = P3[i];
//            t2 = P1[i];
//            t3 = ex[i] * ival;
//            t = t1 - t2 - t3;
//            p3p1i += (t * t);
//        }
//        for (int i = 0; i < P3.length; i++) {
//            t1 = P3[i];
//            t2 = P1[i];
//            t3 = ex[i] * ival;
//            eyy = (t1 - t2 - t3) / Math.sqrt(p3p1i);
//            ey[i] = eyy;
//        }
//        for (int i = 0; i < ey.length; i++) {
//            t1 = ey[i];
//            t2 = p3p1[i];
//            jval += (t1 * t2);
//        }
//        xval = (Math.pow(distance1, 2) - Math.pow(distance2, 2) + Math.pow(d, 2)) / (2 * d);
//        yval = ((Math.pow(distance1, 2) - Math.pow(distance3, 2) + Math.pow(ival, 2) + Math.pow(jval, 2)) / (2 * jval)) - ((ival / jval) * xval);
//
//        t1 = Util.beaconsList.get(0).getLat();
//        t2 = ex[0] * xval;
//        t3 = ey[0] * yval;
//        triptx = t1 + t2 + t3;
//
//        t1 = Util.beaconsList.get(0).getLng();
//        t2 = ex[1] * xval;
//        t3 = ey[1] * yval;
//        tripty = t1 + t2 + t3;
//
//
//        return new Position(triptx, tripty);
//    }

//    public static void calcutate() {
//        double[][] positions = new double[][]{{5.0, -6.0}, {13.0, -15.0}, {21.0, -3.0}, {12.42, -21.2}};
//        double[] distances = new double[]{8.06, 13.97, 23.32, 15.31};
//
//        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
//        Optimum optimum = solver.solve();
//
//// the answer
//        double[] calculatedPosition = optimum.getPoint().toArray();
//
//// error and geometry information
//        RealVector standardDeviation = optimum.getSigma(0);
//        RealMatrix covarianceMatrix = optimum.getCovariances(0);
//    }

}
