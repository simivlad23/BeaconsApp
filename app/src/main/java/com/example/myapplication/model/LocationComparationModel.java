package com.example.myapplication.model;

public class LocationComparationModel {

    private long timeReacord;
    private Position unfiltredPosition;
    private Position meanPosition;
    private Position kalmanPosition;
    private Position armaLoaction;
    private Position actualLocation;

    public LocationComparationModel(long timeReacord,
                                    Position unfiltredPosition,
                                    Position meanPosition,
                                    Position kalmanPosition,
                                    Position armaLoaction,
                                    Position actualLocation) {

        this.timeReacord = System.currentTimeMillis();
        this.unfiltredPosition = unfiltredPosition;
        this.meanPosition = meanPosition;
        this.kalmanPosition = kalmanPosition;
        this.armaLoaction = armaLoaction;
        this.actualLocation = actualLocation;
    }

    public long getTimeReacord() {
        return timeReacord;
    }

    public void setTimeReacord(long timeReacord) {
        this.timeReacord = timeReacord;
    }

    public Position getUnfiltredPosition() {
        return unfiltredPosition;
    }

    public void setUnfiltredPosition(Position unfiltredPosition) {
        this.unfiltredPosition = unfiltredPosition;
    }

    public Position getMeanPosition() {
        return meanPosition;
    }

    public void setMeanPosition(Position meanPosition) {
        this.meanPosition = meanPosition;
    }

    public Position getKalmanPosition() {
        return kalmanPosition;
    }

    public void setKalmanPosition(Position kalmanPosition) {
        this.kalmanPosition = kalmanPosition;
    }

    public Position getArmaLoaction() {
        return armaLoaction;
    }

    public void setArmaLoaction(Position armaLoaction) {
        this.armaLoaction = armaLoaction;
    }

    public Position getActualLocation() {
        return actualLocation;
    }

    public void setActualLocation(Position actualLocation) {
        this.actualLocation = actualLocation;
    }
}
