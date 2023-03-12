package com.example.sc2006_project;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private LatLng carpark_loc;
    private GroundOverlay overlay;
    private LatLngBounds carpark_map_bounds;
    private ArrayList<String> asset_files = new ArrayList<>();
    private ArrayList<String> levels = new ArrayList<>();
    private Button button_carpparklot;
    private Context current = this;
    private GoogleMap googleMap;
    private RelativeLayout wrapper;
    private Spinner level_select;
    private Resources res;
    public int temp;

    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 1){
                        Intent intent = result.getData();
                        if(intent != null){
                            LatLng temp_loc = intent.getParcelableExtra(CarparkLotRecViewAdapter.GET_LOT_COORDS);
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(temp_loc, 15));

                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent intent = getIntent();
        carpark_loc = intent.getParcelableExtra(CarparkRecViewAdapter.COORDS);
        carpark_map_bounds = intent.getParcelableExtra(CarparkRecViewAdapter.BOUND);
        asset_files = (ArrayList<String>) intent.getSerializableExtra(CarparkRecViewAdapter.ASSET_NAMES);
        levels = (ArrayList<String>) intent.getSerializableExtra(CarparkRecViewAdapter.LEVELS);
        res = getResources();
        temp = res.getIdentifier(asset_files.get(0), "raw", getPackageName());

        SupportMapFragment mapfragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        if(mapfragment != null){
            mapfragment.getMapAsync(this);
        }

        level_select = findViewById(R.id.spinner);
        wrapper = findViewById(R.id.spinner_wrapper);
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                levels
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        level_select.setAdapter(adapter);
        level_select.setSelection(0,false);
        if(levels.size() != 1){
            wrapper.setElevation(2);
        }
        level_select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                BitmapDescriptor temp;
                int res_id;
                res = getResources();
                GroundOverlayOptions opts = new GroundOverlayOptions();
                res_id = res.getIdentifier(asset_files.get(i),"raw", getPackageName());
                temp = BitmapDescriptorFactory.fromResource(res_id);
                overlay.remove();
                opts.positionFromBounds(carpark_map_bounds);
                opts.image(temp);
                opts.transparency(0.5f);
                overlay = googleMap.addGroundOverlay(opts);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        button_carpparklot = findViewById(R.id.button);
        button_carpparklot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loadlist = new Intent(current, CarparkLotDisplay.class);
                activityLauncher.launch(loadlist);
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(carpark_loc, 20));
        GroundOverlayOptions opts = new GroundOverlayOptions();
        opts.positionFromBounds(carpark_map_bounds);

        BitmapDescriptor desc = BitmapDescriptorFactory.fromResource(temp);
        opts.image(desc);
        opts.transparency(0.5f);
        overlay = googleMap.addGroundOverlay(opts);
    }

}