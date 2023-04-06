package com.example.sc2006_project.entity;

public class Reservation {
    private String startTime;
    private String endTime;
    private String userID;
    private String parkingLot;

    public Reservation() {
        // Required empty constructor for Firebase Realtime Database
    }

    public Reservation(String startTime, String endTime, String userID, String parkingLot) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.userID = userID;
        this.parkingLot = parkingLot;
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
}
