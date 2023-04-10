package com.example.sc2006_project.entity;

import com.google.android.gms.maps.model.LatLng;

public class Reservation {
    private String startTime;
    private String endTime;
    private String userID;
    private String parkingLot;

    private double latitude, longitude;

    public Reservation() {
        // Required empty constructor for Firebase Realtime Database
    }

    public Reservation(String startTime, String endTime, String userID, String parkingLot, double latitude, double longitude) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.userID = userID;
        this.parkingLot = parkingLot;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getUserID() {
        return userID;
    }

    public String getParkingLot() {
        return parkingLot;
    }

    public double getLat() { return latitude; }

    public double getLong() { return longitude; }
}
