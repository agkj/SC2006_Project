package com.example.sc2006_project;

import com.google.android.gms.maps.model.LatLng;

public class CarparkLot{
    private LatLng position;
    private String name;

    public CarparkLot(LatLng position, String name) {
        this.position = position;
        this.name = name;
    }

    public LatLng getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public void lot_import(){
        //something
    }
}

