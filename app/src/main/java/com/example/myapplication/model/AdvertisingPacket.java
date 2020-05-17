package com.example.myapplication.model;

public class AdvertisingPacket {

    protected byte[] data;
    protected int rssi;
    protected long timestamp;

    public AdvertisingPacket(int rssi) {
        this.rssi = rssi;
        this.timestamp = System.currentTimeMillis();
    }

    public AdvertisingPacket(byte[] data, int rssi) {
        this.data = data;
        this.rssi = rssi;
        this.timestamp = System.currentTimeMillis();
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
