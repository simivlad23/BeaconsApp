package com.example.myapplication;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.util.Log;

import com.example.myapplication.model.RssiRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Beacons {

    private static final String TAG = "BLE-BEACON";
    public static final double WEIGHTED_VALUE = 0.75;

    private BluetoothGatt bluetoothGatt;
    private BluetoothDevice bluetoothDevice;
    private Context context;
    private Timer timier;

    private double lat = 0.0;
    private double lng = 0.0;

    private double rssiValue = 0.0;
    private double averageRssiValue = 1.0;
    private double distanceFormula1 = 1.0;
    private double distanceFormula2 = 1.0;
    private double distanceFormula3 = 1.0;
    private double distanceAverage = 1.0;
    private double distance= 1.0;

    private List<Double> distances = new ArrayList<>();
    public LinkedList<Double> rssiRecords = new LinkedList<>();

    public Beacons(BluetoothDevice bt, Context cnt) {
        rssiRecords.addLast(-50.0);
        this.bluetoothDevice = bt;
        this.context = cnt;
    }

    public void connectToGATT(){
        this.bluetoothGatt = bluetoothDevice.connectGatt(context, true, gattCallback);
    }

    protected BluetoothGattCallback gattCallback = new BluetoothGattCallback() {


        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if (newState == BluetoothGatt.STATE_CONNECTED) {
                Log.i(TAG, "onConnectionStateChange() - STATE_CONNECTED   " + gatt.getDevice().getAddress());
                bluetoothGatt = gatt;
                timier = new Timer();
                timier.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                         boolean rssiReadStatus  = bluetoothGatt.readRemoteRssi();
                         Log.i("STATUS READ ", "Request rssi vale from device "+ bluetoothDevice.getAddress() + "   at time: " + Util.convertFromEpochToDate() + "and staus is "+ rssiReadStatus );
                    }
                },0, 50);
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                Log.i(TAG, "onConnectionStateChange() - STATE_DISCONNECTED  " + gatt.getDevice().getAddress());
                timier.cancel();
                timier = null;
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
           // super.onReadRemoteRssi(gatt, rssi, status);

            if (status == BluetoothGatt.GATT_SUCCESS) {

                String deviceName = gatt.getDevice().getName();
                String deviceAddress = gatt.getDevice().getAddress();


                Date date = Util.convertFromEpochToDate();

                double newRssiValue = WEIGHTED_VALUE * rssi + rssiRecords.getLast() * (1 - WEIGHTED_VALUE);

                double distanceCalculated2 = Util.getDistance2(newRssiValue, -61);
                double distanceCalculated3 = Util.getDistance3(newRssiValue, -63);

                rssiRecords.add(newRssiValue);
                if (rssiRecords.size() > 10) {
                    rssiRecords.remove();
                }

                Util.recordsList.add(new RssiRecord(deviceName, deviceAddress, rssi, distanceCalculated3, date));
                Log.d(TAG, String.format("BluetoothGatt ReadRssi from " + gatt.getDevice().getName() + " value " + newRssiValue + "  and distance calculated :" + Util.getDistance3(newRssiValue, -59)));


                //distances.add(distanceCalculated);
                distanceFormula2 = distanceCalculated2;
                distanceFormula3 = distanceCalculated3;

                distances.add(distanceCalculated3);

                setAverageBleRssi();
                distanceAverage = Util.getDistance3(averageRssiValue, -59);
                rssiValue = Double.parseDouble(Util.df.format(newRssiValue));


            }
        }
    };

    public void stopReacording() {
        if (timier != null) {
            timier.cancel();
        }
        bluetoothGatt.close();
        bluetoothGatt = null;

    }

    public double setAverageBleRssi() {
        //TODO delete min and max
        double sum = 0.0;
        for (Double rssi : rssiRecords) {
            sum += rssi;
        }

        double average = sum / rssiRecords.size();
        this.averageRssiValue = average;
        return average;
    }

    public double setAverageBleDistance(){

        double sum = 0.0;
        for(Double distance : distances)
        {
            sum += distance;
        }

        double average = sum / distances.size();
        distances.clear();
        this.distance = average;
        return average;
    }

    public void smootingAlgoritm(ScanResult result){

        double newRssiValue = WEIGHTED_VALUE * result.getRssi() + rssiRecords.getLast() * (1 - WEIGHTED_VALUE);

        distanceFormula2 = Util.getDistance2(newRssiValue, Util.TX_POWER);
        distanceFormula3 = Util.getDistance3(newRssiValue, Util.TX_POWER);

        rssiRecords.addLast(newRssiValue);

        if (rssiRecords.size() > 10) {
            rssiRecords.removeFirst();
        }


       // Util.recordsList.add(new RssiRecord(bluetoothDevice.getName(), bluetoothDevice.getAddress(), newRssiValue, distanceCalculated3, date));
        //distances.add(distanceCalculated);
        distances.add(distanceFormula3);

        setAverageBleRssi();
        distanceAverage = Util.getDistance3(averageRssiValue, -61);
        rssiValue = Double.parseDouble(Util.df.format(newRssiValue));
    }

    public BluetoothGatt getBluetoothGatt() {
        return bluetoothGatt;
    }

    public void setBluetoothGatt(BluetoothGatt bluetoothGatt) {
        this.bluetoothGatt = bluetoothGatt;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getRssiValue() {
        return rssiValue;
    }

    public void setRssiValue(int rssiValue) {
        this.rssiValue = rssiValue;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setRssiValue(double rssiValue) {
        this.rssiValue = rssiValue;
    }

    public double getAverageRssiValue() {
        return averageRssiValue;
    }

    public void setAverageRssiValue(double averageRssiValue) {
        this.averageRssiValue = averageRssiValue;
    }

    public double getDistanceFormula1() {
        return distanceFormula1;
    }

    public void setDistanceFormula1(double distanceFormula1) {
        this.distanceFormula1 = distanceFormula1;
    }

    public double getDistanceFormula2() {
        return distanceFormula2;
    }

    public void setDistanceFormula2(double distanceFormula2) {
        this.distanceFormula2 = distanceFormula2;
    }

    public double getDistanceFormula3() {
        return distanceFormula3;
    }

    public void setDistanceFormula3(double distanceFormula3) {
        this.distanceFormula3 = distanceFormula3;
    }

    public double getDistanceAverage() {
        return distanceAverage;
    }

    public void setDistanceAverage(double distanceAverage) {
        this.distanceAverage = distanceAverage;
    }

    public List<Double> getDistances() {
        return distances;
    }

    public void setDistances(List<Double> distances) {
        this.distances = distances;
    }
}
