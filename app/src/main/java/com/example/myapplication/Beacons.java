package com.example.myapplication;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.util.Log;

import com.example.myapplication.model.AdvertisingPacket;
import com.example.myapplication.model.BeaconRecord;
import com.example.myapplication.model.RssiRecord;
import com.example.myapplication.util.BeaconDistanceCalculator;
import com.example.myapplication.util.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Beacons {

    private static final String TAG = "BLE-BEACON";
    public static final double WEIGHTED_VALUE = 0.8;
    public static final long MAXIMUM_PACKET_AGE = TimeUnit.SECONDS.toMillis(30);

    private BluetoothGatt bluetoothGatt;
    private BluetoothDevice bluetoothDevice;
    private BeaconRecord beaconRecord;
    private Context context;
    private Timer timier;

    private double lat = 0.0;
    private double lng = 0.0;

    private double rssiValue = 0.0;
    private double kalmanRssi = 0.0;
    private double meanRssi = 0.0;
    private double armaRssi = 0.0;

    private double rssiDist = 1.0;
    private double kalmanDist = 1.0;
    private double meanDist = 1.0;
    private double armaDist = 1.0;

    private double rssiDist2 = 1.0;
    private double kalmanDist2 = 1.0;
    private double meanDist2 = 1.0;
    private double armaDist2 = 1.0;

    private double averageRssiValue = 1.0;
    private double distanceFormula1 = 1.0;
    private double distanceFormula2 = 1.0;
    private double distanceFormula3 = 1.0;
    private double distanceAverage = 1.0;
    private double distance = 1.0;

    private List<Double> distances = new ArrayList<>();
    public LinkedList<Double> rssiRecords = new LinkedList<>();
    public List<AdvertisingPacket> advertisingPackets = new ArrayList<>();

    public Beacons(BluetoothDevice bt, Context cnt) {
        rssiRecords.addLast(-50.0);
        beaconRecord = new BeaconRecord();
        this.bluetoothDevice = bt;
        this.context = cnt;
    }

    public void connectToGATT() {
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
                        boolean rssiReadStatus = bluetoothGatt.readRemoteRssi();
                        Log.i("STATUS READ ", "Request rssi vale from device " + bluetoothDevice.getAddress() + "   at time: " + Util.convertFromEpochToDate() + "and staus is " + rssiReadStatus);
                    }
                }, 0, 50);
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

//                double distanceCalculated2 = Util.getDistance2(newRssiValue, -61);
//                double distanceCalculated3 = Util.getDistance3(newRssiValue, -63);

                double distanceCalculated2 = BeaconDistanceCalculator.calculateDistance((float) newRssiValue);
                double distanceCalculated3 = BeaconDistanceCalculator.calculateDistanceFormula2(newRssiValue);

                rssiRecords.add(newRssiValue);
                if (rssiRecords.size() > 10) {
                    rssiRecords.remove();
                }

                Util.recordsList.add(new RssiRecord(deviceName, deviceAddress, rssi, distanceCalculated3, date));
                Log.d(TAG, String.format("BluetoothGatt ReadRssi from " + gatt.getDevice().getName() + " value " + newRssiValue + "  and distance calculated :" + BeaconDistanceCalculator.calculateDistanceFormula2(newRssiValue)));


                //distances.add(distanceCalculated);
                distanceFormula2 = distanceCalculated2;
                distanceFormula3 = distanceCalculated3;
                distances.add(distanceCalculated3);
                setAverageBleRssi();
                distanceAverage = BeaconDistanceCalculator.calculateDistanceFormula2(averageRssiValue);
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
        double sum = 0.0;
        for (Double rssi : rssiRecords) {
            sum += rssi;
        }

        double average = sum / rssiRecords.size();
        this.averageRssiValue = average;
        return average;
    }

    public void smootingAlgoritm(ScanResult result) {

        double newRssiValue = WEIGHTED_VALUE * result.getRssi() + rssiRecords.getLast() * (1 - WEIGHTED_VALUE);
        distanceFormula2 = BeaconDistanceCalculator.calculateDistance((float) newRssiValue);
        distanceFormula3 = BeaconDistanceCalculator.calculateDistanceFormula2(newRssiValue);

        rssiRecords.addLast(newRssiValue);
        if (rssiRecords.size() > 5) {
            rssiRecords.removeFirst();
        }

        // Util.recordsList.add(new RssiRecord(bluetoothDevice.getName(), bluetoothDevice.getAddress(), newRssiValue, distanceCalculated3, date));
        //distances.add(distanceCalculated);
        distances.add(distanceFormula3);

        setAverageBleRssi();
        distanceAverage = BeaconDistanceCalculator.calculateDistanceFormula2(averageRssiValue);
        rssiValue = Double.parseDouble(Util.df.format(newRssiValue));

        beaconRecord.setTimeReacord(Util.convertFromEpochToDate());
        beaconRecord.setReadRssi(result.getRssi());
        beaconRecord.setSmootRssi(newRssiValue);
        beaconRecord.setMeanRssi(averageRssiValue);
        beaconRecord.setSmootDistance(distanceFormula3);
        beaconRecord.setMeanDistance(distanceAverage);

        //Firebase wtite
        //Util.db.collection("beacons_records").add(beaconRecord);

    }

    public ArrayList<AdvertisingPacket> getAdvertisingPacketsBetween(long startTimestamp, long endTimestamp) {
        // check if advertising packets are available
        synchronized (advertisingPackets) {


            if (advertisingPackets.isEmpty()) {
                return new ArrayList<>();
            }

            AdvertisingPacket oldestAdvertisingPacket = advertisingPackets.get(0);
            AdvertisingPacket latestAdvertisingPacket = advertisingPackets.get(advertisingPackets.size() - 1);

            // check if the timestamps are out of range
            if (endTimestamp <= oldestAdvertisingPacket.getTimestamp() || startTimestamp > latestAdvertisingPacket.getTimestamp()) {
                return new ArrayList<>();
            }

            AdvertisingPacket midstAdvertisingPacket = advertisingPackets.get(advertisingPackets.size() / 2);

            // find the index of the first advertising packet with a timestamp
            // larger than or equal to the specified startTimestamp
            int startIndex = 0;
            if (startTimestamp > oldestAdvertisingPacket.getTimestamp()) {
                // figure out if the start timestamp is before or after the midst advertising packet
                ListIterator<AdvertisingPacket> listIterator;
                if (startTimestamp < midstAdvertisingPacket.getTimestamp()) {
                    // start timestamp is in the first half of advertising packets
                    // start iterating from the beginning
                    listIterator = advertisingPackets.listIterator();
                    while (listIterator.hasNext()) {
                        if (listIterator.next().getTimestamp() >= startTimestamp) {
                            startIndex = listIterator.previousIndex();
                            break;
                        }
                    }
                } else {
                    // start timestamp is in the second half of advertising packets
                    // start iterating from the end
                    listIterator = advertisingPackets.listIterator(advertisingPackets.size());
                    while (listIterator.hasPrevious()) {
                        if (listIterator.previous().getTimestamp() < startTimestamp) {
                            startIndex = listIterator.nextIndex() + 1;
                            break;
                        }
                    }
                }
            }

            // find the index of the last advertising packet with a timestamp
            // smaller than the specified endTimestamp
            int endIndex = advertisingPackets.size();
            if (endTimestamp < latestAdvertisingPacket.getTimestamp()) {
                // figure out if the end timestamp is before or after the midst advertising packet
                ListIterator<AdvertisingPacket> listIterator;
                if (endTimestamp < midstAdvertisingPacket.getTimestamp()) {
                    // end timestamp is in the first half of advertising packets
                    // start iterating from the beginning
                    listIterator = advertisingPackets.listIterator(startIndex);
                    while (listIterator.hasNext()) {
                        if (listIterator.next().getTimestamp() >= endTimestamp) {
                            endIndex = listIterator.previousIndex();
                            break;
                        }
                    }
                } else {
                    // end timestamp is in the second half of advertising packets
                    // start iterating from the end
                    listIterator = advertisingPackets.listIterator(advertisingPackets.size());
                    while (listIterator.hasPrevious()) {
                        if (listIterator.previous().getTimestamp() < endTimestamp) {
                            endIndex = listIterator.nextIndex() + 1;
                            break;
                        }
                    }
                }
            }

            return new ArrayList<>(advertisingPackets.subList(startIndex, endIndex));
        }
    }

    public void trimAdvertisingPackets() {
        synchronized (advertisingPackets) {
            if (advertisingPackets.isEmpty()) {
                return;
            }
            List<AdvertisingPacket> removableAdvertisingPackets = new ArrayList<>();
            AdvertisingPacket latestAdvertisingPacket = advertisingPackets.get(advertisingPackets.size() - 1);
            long minimumPacketTimestamp = System.currentTimeMillis() - MAXIMUM_PACKET_AGE;
            for (AdvertisingPacket advertisingPacket : advertisingPackets) {
                if (advertisingPacket == latestAdvertisingPacket) {
                    continue;
                }
                if (advertisingPacket.getTimestamp() < minimumPacketTimestamp) {
                    removableAdvertisingPackets.add(advertisingPacket);
                }
            }

            advertisingPackets.removeAll(removableAdvertisingPackets);
        }
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

    public double getKalmanRssi() {
        return kalmanRssi;
    }

    public void setKalmanRssi(double kalmanRssi) {
        this.kalmanRssi = kalmanRssi;
    }

    public static String getTAG() {
        return TAG;
    }

    public double getMeanRssi() {
        return meanRssi;
    }

    public void setMeanRssi(double meanRssi) {
        this.meanRssi = meanRssi;
    }

    public double getRssiDist() {
        return rssiDist;
    }

    public void setRssiDist(double rssiDist) {
        this.rssiDist = rssiDist;
    }

    public double getKalmanDist() {
        return kalmanDist;
    }

    public void setKalmanDist(double kalmanDist) {
        this.kalmanDist = kalmanDist;
    }

    public double getMeanDist() {
        return meanDist;
    }

    public void setMeanDist(double meanDist) {
        this.meanDist = meanDist;
    }

    public double getArmaRssi() {
        return armaRssi;
    }

    public void setArmaRssi(double armaRssi) {
        this.armaRssi = armaRssi;
    }

    public double getArmaDist() {
        return armaDist;
    }

    public void setArmaDist(double armaDist) {
        this.armaDist = armaDist;
    }

    public List<AdvertisingPacket> getAdvertisingPackets() {
        return advertisingPackets;
    }

    public void setAdvertisingPackets(List<AdvertisingPacket> advertisingPackets) {
        this.advertisingPackets = advertisingPackets;
    }

    public double getRssiDist2() {
        return rssiDist2;
    }

    public void setRssiDist2(double rssiDist2) {
        this.rssiDist2 = rssiDist2;
    }

    public double getKalmanDist2() {
        return kalmanDist2;
    }

    public void setKalmanDist2(double kalmanDist2) {
        this.kalmanDist2 = kalmanDist2;
    }

    public double getMeanDist2() {
        return meanDist2;
    }

    public void setMeanDist2(double meanDist2) {
        this.meanDist2 = meanDist2;
    }

    public double getArmaDist2() {
        return armaDist2;
    }

    public void setArmaDist2(double armaDist2) {
        this.armaDist2 = armaDist2;
    }
}
