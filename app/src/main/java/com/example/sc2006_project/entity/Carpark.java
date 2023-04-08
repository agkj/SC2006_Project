package com.example.sc2006_project.entity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.Serializable;
import java.util.ArrayList;

public class Carpark implements Serializable {
    private LatLng location_coord;
    private LatLngBounds asset_bound;
    private String location_name;
    private String asset_name;

    private String location_lot;
    private ArrayList<String> asset_list = new ArrayList<>();

    private ArrayList<String> level_list = new ArrayList<>();

    public String getAsset_name() {
        return asset_name;
    }

    public void setAsset_name(String asset_name) {
        this.asset_name = asset_name;
    }

    /**
     * Class constructor with simplified parameters of only coordinates and name of carpark.
     * @param location_coord LatLng coordinates of carpark.
     * @param location_name Name of carpark.
     * @author Chin Han Wen
     */
    public Carpark(LatLng location_coord, String location_name) {
        this.location_coord = location_coord;
        this.location_name = location_name;
    }

    public Carpark(LatLng location_coord, String location_name, String location_lot) {
        this.location_coord = location_coord;
        this.location_name = location_name;
        this.location_lot = location_lot;
    }

    /**
     * Class constructor with more parameters, defining a carpark as well as the map overlays to be shown.
     * @param location_coord LatLng coordinates of carpark.
     * @param asset_bound LatLngBound of map overlay.
     * @param location_name Name of carpark.
     * @param asset_list String array list of the file names of the map overlay image files.
     * @param level_list String array list of the level list in a multi-floor carpark.
     * @param location_lot Number of carpark.
     * @author Chin Han Wen
     */
    public Carpark(LatLng location_coord, LatLngBounds asset_bound, String location_name, ArrayList<String> asset_list, ArrayList<String> level_list, String location_lot) {
        this.location_coord = location_coord;
        this.asset_bound = asset_bound;
        this.location_name = location_name;
        this.asset_list = asset_list;
        this.level_list = level_list;
        this.location_lot = location_lot;
    }

    public LatLng getLocation_coord() {
        return location_coord;
    }

    public String getLocation_name() {
        return location_name;
    }

    public ArrayList<String> getAsset_list() {
        return asset_list;
    }

    public void setAsset_list(ArrayList<String> asset_list) {
        this.asset_list = asset_list;
    }

    public void setLocation_coord(LatLng location_coord) {
        this.location_coord = location_coord;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public void setLocation_lot(String location_lot){ this.location_lot = location_lot;}

    public ArrayList<String> getLevel_list() {
        return level_list;
    }

    public void setLevel_list(ArrayList<String> level_list) {
        this.level_list = level_list;
    }

    public LatLngBounds getAsset_bound(){
        return asset_bound;
    }

    public void setAsset_bound(LatLngBounds asset_bound){
        this.asset_bound = asset_bound;
    }

    public void carpark_import(){
        //Something
    }
}

