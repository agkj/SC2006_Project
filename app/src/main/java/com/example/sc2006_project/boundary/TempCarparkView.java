package com.example.sc2006_project;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import java.util.ArrayList;


public class TempCarparkView extends AppCompatActivity{
    private RecyclerView carparkRecView;
    private ArrayList<Carpark> carparks = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp_carpark_view);
        GoogleApiAvailability checker = new GoogleApiAvailability();
        if(checker.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS){
            ArrayList<String> asset_list = new ArrayList<>();
            asset_list.add("carpark_ntu_c");
            asset_list.add("bonk");
            ArrayList<String> level_list = new ArrayList<>();
            level_list.add("L1");
            level_list.add("L2");
            level_list.add("L3");
            ArrayList<String> test1 = new ArrayList<>();
            test1.add("carpark_ntu_f");
            ArrayList<String> test2 = new ArrayList<>();
            test2.add("L1");
            carparkRecView = findViewById(R.id.carparkRecView);
            carparks.add(new Carpark(new LatLng(1.345275,103.683411),"Carpark Q/Student Services Centre"));
            carparks.add(new Carpark(
                    new LatLng(1.346776,103.683368),
                    new LatLngBounds(new LatLng(1.34635, 103.68311), new LatLng(1.34787, 103.6837)),
                    "Carpark F/Mat Sci Carpark",
                    test1,
                    test2));
            carparks.add(new Carpark(
                    new LatLng(1.345720,103.681330),
                    new LatLngBounds(new LatLng(1.3455,103.68096),new LatLng(1.346,103.68234)),
                    "Carpark C/Some Rando",
                    asset_list,
                    level_list));
            CarparkRecViewAdapter adapter = new CarparkRecViewAdapter(this);
            adapter.setCarparks(carparks);
            carparkRecView.setAdapter(adapter);
            carparkRecView.setLayoutManager(new LinearLayoutManager(this));
        }
        else{
            Toast.makeText(this, "This app cannot run unless Google Play Services are installed.", Toast.LENGTH_LONG).show();
            this.finish();
            System.exit(0);
        }
    }
}
