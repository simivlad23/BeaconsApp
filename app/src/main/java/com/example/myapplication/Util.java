package com.example.myapplication;

import android.content.Context;
import android.graphics.Point;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.model.Position;
import com.example.myapplication.model.RssiRecord;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

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
    public static final double MARGIN_UP = 20;
    public static final double MARGIN_LEFT = 20;
    public static int SCREEN_X = 1080;
    public static int SCREEN_Y = 2107;

    public static double FOOR_WIDE = 8.2;
    public static double FLOOR_HEIGHT = 12;
    public static double FOOR_WIDE_CM = 810;
    public static double FLOOR_HEIGHT_CM = 1200;
    public static double WALL_WIDTH = 20;

    public static int NUM_BLOCKS_WIDE = 100;
    public static int NUM_BLOCK_HIGH = 200;
    public static int BLOCK_SIZE = 50;

    public static double PIXELS_PER_CM_X = SCREEN_X / FOOR_WIDE_CM;
    public static double PIXELS_PER_CM_Y = SCREEN_Y / FLOOR_HEIGHT_CM;

    public static FirebaseFirestore db;

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
        double distance = Math.pow(10d, ((double) txPower - rssi) / (10 * 2));
        return distance;
    }

    public static double getDistance3(double rssi, int txPower) {

        //hard coded power value. Usually ranges between -59 to -65
        if (rssi == 0) {
            return -1.0;
        }

        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            double distance = Math.pow(ratio, 10);
            return distance * 100;
        } else {
            double distance = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return distance * 100;
        }
    }

    public static void makeCustomToast(String message, Context context) {
        final Toast mToastToShow;
        int toastDurationInMilliSeconds = 100;
        mToastToShow = Toast.makeText(context, "Hello world, I am a toast.", Toast.LENGTH_LONG);

        CountDownTimer toastCountDown;
        toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, 1000 /*Tick duration*/) {
            public void onTick(long millisUntilFinished) {
                mToastToShow.show();
            }

            public void onFinish() {
                mToastToShow.cancel();
            }
        };

        mToastToShow.show();
        toastCountDown.start();

    }

    public static void makeTaost(String message, Context context) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static Date convertFromEpochToDate() {
        long timeNow = System.currentTimeMillis();
        Date dateNow = new Date(timeNow);
        return dateNow;
    }

    public static Position calcutateBasedNowRssi() {

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

        //error and geometry information
        RealVector standardDeviation = optimum.getSigma(0);
        RealMatrix covarianceMatrix = optimum.getCovariances(0);

        Log.i("NOW_POSITION", "x: " + calculatedPosition[0] + " " + "y: " + calculatedPosition[1]);
        return new Position(calculatedPosition[0], calculatedPosition[1]);

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
        RealVector standardDeviation = optimum.getSigma(0);
        RealMatrix covarianceMatrix = optimum.getCovariances(0);

        Log.i("MEAN_POSITION", "x: " + calculatedPosition[0] + " " + "y: " + calculatedPosition[1]);
        return new Position(calculatedPosition[0], calculatedPosition[1]);
    }

    public static void setBeaconsPosition() {

        for (Beacons beacons : beaconsMap.values()) {
            switch (beacons.getBluetoothDevice().getAddress()) {
                case "C7:7E:A2:BD:51:4C":
                    beacons.setLat(0);
                    beacons.setLng(0);
                    break;
                case "D2:83:6A:5E:AB:F8":
                    beacons.setLat(300);
                    beacons.setLng(0);
                    break;
                case "D1:A4:D2:15:51:00":
                    beacons.setLat(300);
                    beacons.setLng(400);
                    break;
                case "C0:08:B4:0E:37:0E":
                    beacons.setLat(0);
                    beacons.setLng(400);
                    break;
                case "C8:26:E3:CE:42:5C":
                    beacons.setLat(210);
                    beacons.setLng(260);
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

        Position position1 = new Position(100, 100);
        Position position2 = new Position(200, 200);
        Position position3 = new Position(300, 300);
        Position position4 = new Position(300, 400);

        Util.testPosition.add(convertFromCmToPixels(position1.getLat(), position1.getLng()));
        Util.testPosition.add(convertFromCmToPixels(position2.getLat(), position2.getLng()));
        Util.testPosition.add(convertFromCmToPixels(position3.getLat(), position3.getLng()));
        Util.testPosition.add(convertFromCmToPixels(position4.getLat(), position4.getLng()));
    }

    public static void initBeaconAndTestPositions() {

        BLOCK_SIZE = SCREEN_X / NUM_BLOCKS_WIDE;
        Util.NUM_BLOCK_HIGH = SCREEN_Y / BLOCK_SIZE;

        for (Beacons beacons : beaconsMap.values()) {
            Point scalePostion = convertFromCmToPixels(beacons.getLat(), beacons.getLng());
            beaconsPosition.add(scalePostion);
        }
        setTestPosition();
    }

    public static Point convertFromCmToPixels(double x, double y) {

        int newXPixel = (int) ((x + MARGIN_LEFT) * PIXELS_PER_CM_X);
        int newYPixel = (int) ((y + MARGIN_UP) * PIXELS_PER_CM_Y);

        return new Point(newXPixel, newYPixel);
    }

    public static Point convertFromPixelToCm(double x, double y) {

        int newXPixel = (int) ((x / PIXELS_PER_CM_X) - MARGIN_UP);
        int newYPixel = (int) ((y / PIXELS_PER_CM_Y) - MARGIN_LEFT);

        return new Point(newXPixel, newYPixel);
    }
}
