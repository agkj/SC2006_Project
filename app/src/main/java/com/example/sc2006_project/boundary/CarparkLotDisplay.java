package com.example.sc2006_project.boundary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.sc2006_project.R;
import com.example.sc2006_project.control.CarparkLotRecViewAdapter;
import com.example.sc2006_project.entity.CarparkLot;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
