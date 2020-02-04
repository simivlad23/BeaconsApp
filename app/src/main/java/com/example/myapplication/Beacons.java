package com.example.myapplication;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;
import android.util.Log;

import com.example.myapplication.model.RssiRecord;

import java.util.Timer;
import java.util.TimerTask;

public class Beacons {

    private static final String TAG = "BLE-BEACON";
    private BluetoothGatt bluetoothGatt;
    private BluetoothDevice bluetoothDevice;
    private Timer timier;

    public Beacons(BluetoothDevice bt, Context cnt) {
        this.bluetoothGatt = bt.connectGatt(cnt, true, gattCallback);
        this.bluetoothDevice = bt;
    }

    protected BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            Log.i(TAG, "onConnectionStateChange() newSatus  -   " + newState + "  on device " + gatt.getDevice().getAddress());
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                Log.i(TAG, "onConnectionStateChange() - STATE_CONNECTED   " + gatt.getDevice().getAddress());
                bluetoothGatt = gatt;
                timier = new Timer();
                timier.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        bluetoothGatt.readRemoteRssi();
                    }
                }, 0, 1000);

                gatt.discoverServices();
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                Log.i(TAG, "onConnectionStateChange() - STATE_DISCONNECTED  " + gatt.getDevice().getAddress());
                timier.cancel();
                timier = null;
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);

            if (status == BluetoothGatt.GATT_SUCCESS) {

                String deviceName = gatt.getDevice().getName();
                String deviceAddress = gatt.getDevice().getAddress();
                double distanceCalculated = Util.getDistance(rssi, 4);

                Util.recordsList.add(new RssiRecord(deviceName, deviceAddress, rssi, distanceCalculated));
                Log.d(TAG, String.format("BluetoothGatt ReadRssi from " + gatt.getDevice().getAddress() + " value : [%d]  and distance calculated :" + Util.getDistance(rssi, 1), rssi));
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
}
