package com.example.myapplication;

import android.bluetooth.le.ScanResult;
import android.content.Context;
//import android.graphics.Point;
//import android.icu.text.Edits;
//import android.net.wifi.ScanResult;
import android.graphics.Point;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.model.Position;
import com.example.myapplication.model.RssiRecord;
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {

    private static final String TAG = "UTIL";

    private static final double RF_A = 23; // the absolute energy which is represent by dBm at a distance of 1 meter from the transmitter
    private static final double RF_N = 2.3; // n is the signal transmission constant
    public static final int TX_POWER = -61;
    public static final long FIVE_SECOND_MILISECOND = 10000l;
    public static final double MARGIN_UP = 0.85;
    public static final double MARGIN_LEFT = 0.85;
    public static int SCREEN_X = 1080;
    public static int SCREEN_y = 2107;

    public static double FOOR_WIDE = 8.2;
    public static double FLOOR_HEIGHT = 12;
    public static int NUM_BLOCKS_WIDE = 100;
    public static int NUM_BLOCK_HIGH = 50;
    public static int BLOCK_SIZE = 50;


    public static Map<String, Beacons> beaconsMap = new HashMap<>();
    public static List<Beacons> beaconsList = new ArrayList<>();
    public static DecimalFormat df = new DecimalFormat("#.000");
    public static List<RssiRecord> recordsList = new ArrayList<>();
    public static List<Position> positionsList = new ArrayList<>();

    public static List<Point> beaconsPosition = new ArrayList<>();
    public static List<Point> testPosition = new ArrayList<>();


    public static double getDistance(double rssi, double txPower) {
        return (Math.pow(10d, -((rssi + RF_A) / 10 * RF_N))) / 10.0;
    }

    public static double getDistance2(double rssi, int txPower) {
        double distance = Math.pow(10d, ((double) txPower - rssi) / (10 * 2)) / 100.0;
        return Double.parseDouble(df.format(distance));
    }

    public static double getDistance3(double rssi, int txPower) {

        //hard coded power value. Usually ranges between -59 to -65
        if (rssi == 0) {
            return -1.0;
        }

        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            double distance = Math.pow(ratio, 10);
            return Double.parseDouble(df.format(distance));
        } else {
            double distance = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return Double.parseDouble(df.format(distance));
        }
    }

    public static void makeTaost(String message, Context context) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static Date convertFromEpochToDate(long time) {

        long timeNow = System.currentTimeMillis();
        Date dateNow = new Date(timeNow);
        return dateNow;
    }

    public static Position calcutateBasedNowRssi()  {


        double[][] positions = new double[beaconsMap.size()][2];
        double[] distances = new double[beaconsMap.size()];

        int index = 0;

        for (Beacons beacons : beaconsMap.values()) {

            positions[index][0] = beacons.getLat();
            positions[index][1] = beacons.getLng();

            distances[index] = beacons.getDistanceFormula3();
            index++;
        }

        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
        LeastSquaresOptimizer.Optimum optimum = solver.solve();

        // the answer
        double[] calculatedPosition = optimum.getPoint().toArray();

        // error and geometry information
        //RealVector standardDeviation = optimum.getSigma(0);
        //RealMatrix covarianceMatrix = optimum.getCovariances(0);

        Log.i("NOW_POSITION", "x: " + calculatedPosition[0] + " " + "y: " + calculatedPosition[1]);
        return new Position(Double.parseDouble(df.format(calculatedPosition[0])), Double.parseDouble(df.format(calculatedPosition[1])));

    }

    public static Position calcutateBasedMeanRssi() {

        double[][] positions = new double[beaconsMap.size()][2];
        double[] distances = new double[beaconsMap.size()];

        int index = 0;

        for (Beacons beacons : beaconsMap.values()) {

            positions[index][0] = beacons.getLat();
            positions[index][1] = beacons.getLng();

            distances[index] = beacons.getDistanceAverage();
            index++;
        }

        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
        LeastSquaresOptimizer.Optimum optimum = solver.solve();

        // the answer
        double[] calculatedPosition = optimum.getPoint().toArray();

        // error and geometry information
        //RealVector standardDeviation = optimum.getSigma(0);
        //RealMatrix covarianceMatrix = optimum.getCovariances(0);

        Log.i("MEAN_POSITION", "x: " + calculatedPosition[0] + " " + "y: " + calculatedPosition[1]);
        return new Position(Double.parseDouble(df.format(calculatedPosition[0])), Double.parseDouble(df.format(calculatedPosition[1])));

    }

    public static void setBeaconsPosition() {

        for (Beacons beacons : beaconsMap.values()) {
            switch (beacons.getBluetoothDevice().getAddress()) {
                case "C7:7E:A2:BD:51:4C":
                    beacons.setLat(0);
                    beacons.setLng(0);
                    break;
                case "D2:83:6A:5E:AB:F8":
                    beacons.setLat(4.2);
                    beacons.setLng(5.2);
                    break;
                case "D1:A4:D2:15:51:00":
                    beacons.setLat(4.2);
                    beacons.setLng(0);
                    break;
                case "C0:08:B4:0E:37:0E":
                    beacons.setLat(0);
                    beacons.setLng(5.2);
                    break;
                case "C8:26:E3:CE:42:5C":
                    beacons.setLat(2.1);
                    beacons.setLng(2.6);
                    break;
            }
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

    public static void setTestPosition() {

        Position position1 = new Position(1.0, 4.0);
        Position position2 = new Position(2.0, 3.0);
        Position position3 = new Position(3.0, 2.0);
        Position position4 = new Position(4.0, 1.0);

        Util.testPosition.add(convertCoordinates(position1.getLat(),position1.getLng()));
        Util.testPosition.add(convertCoordinates(position2.getLat(),position2.getLng()));
        Util.testPosition.add(convertCoordinates(position3.getLat(),position3.getLng()));
        Util.testPosition.add(convertCoordinates(position4.getLat(),position4.getLng()));
    }

    public static void initBeaconAndTestPositions(){

        BLOCK_SIZE =  SCREEN_X / NUM_BLOCKS_WIDE;
        Util.NUM_BLOCK_HIGH = SCREEN_y / BLOCK_SIZE;

        for(Beacons beacons: beaconsMap.values()){
            Point scalePostion = convertCoordinates(beacons.getLat(),beacons.getLng());
            beaconsPosition.add(scalePostion);
        }
        setTestPosition();
    }

    public static Point convertCoordinates(double x, double y) {
        //added 1 because of margin
        double scaleX = x / FOOR_WIDE;
        double scaleY = y / FLOOR_HEIGHT;

        int newX = (int) (scaleX * NUM_BLOCKS_WIDE);
        int newY = (int) (scaleY * NUM_BLOCK_HIGH);

        return new Point(newX,newY);
    }

}
