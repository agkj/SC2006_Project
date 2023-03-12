package com.example.sc2006_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class CarparkLotDisplay extends AppCompatActivity {
    private RecyclerView carparklotrecview;
    private ArrayList<CarparkLot> lots = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_carpark_lot_display);

        carparklotrecview = findViewById(R.id.carparkLotRecView);
        lots.add(new CarparkLot(new LatLng(13,10),"Name 1111"));
        lots.add(new CarparkLot(new LatLng(11,12), "Name 2222"));

        CarparkLotRecViewAdapter adapter = new CarparkLotRecViewAdapter(this, CarparkLotDisplay.this);
        adapter.setLots(lots);

        carparklotrecview.setAdapter(adapter);
        carparklotrecview.setLayoutManager(new LinearLayoutManager(this));
    }
}