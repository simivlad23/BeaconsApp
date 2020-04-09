package com.example.myapplication;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;
import android.util.Log;

import com.example.myapplication.model.RssiRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Beacons {

    private static final String TAG = "BLE-BEACON";
    private BluetoothGatt bluetoothGatt;
    private BluetoothDevice bluetoothDevice;
    private Context context;
    private Timer timier;

    private double lat = 0.0;
    private double lng = 0.0;
    private int rssiValue;
    private double distance= 1.0;
    private List<Double> distances = new ArrayList<>();



    public Beacons(BluetoothDevice bt, Context cnt) {
        this.bluetoothDevice = bt;
        this.context = cnt;


    }

    public void connectToGATT(){
        this.bluetoothGatt = bluetoothDevice.connectGatt(context, true, gattCallback);
    }

    protected void startReading(){
        timier = new Timer();
        timier.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                bluetoothGatt.readRemoteRssi();

            }
        }, Util.startDateReading, 1000);
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
                         Log.i("STATUS READ ", "Request rssi vale from device "+ bluetoothDevice.getAddress() + "   at time: " + Util.convertFromEpochToDate(0) + "and staus is "+ rssiReadStatus );
                    }
                },0, 300);
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
                double distanceCalculated = Util.getDistance2(rssi, 4);
                Date date = Util.convertFromEpochToDate(0);

                Util.recordsList.add(new RssiRecord(deviceName, deviceAddress, rssi, distanceCalculated,date));
                Log.d(TAG, String.format("BluetoothGatt ReadRssi from " + gatt.getDevice().getAddress() + " value : [%d]  and distance calculated :" + Util.getDistance2(rssi, 4), rssi));


                //distances.add(distanceCalculated);
                distance = distanceCalculated;
                rssiValue = rssi;

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

    public int getRssiValue() {
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
}
