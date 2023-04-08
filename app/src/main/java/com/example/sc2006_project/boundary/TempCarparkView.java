package com.example.sc2006_project.boundary;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sc2006_project.R;
import com.example.sc2006_project.control.CarparkRecViewAdapter;
import com.example.sc2006_project.control.UraDBController;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.example.sc2006_project.entity.Carpark;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//TD: Add a wait thingy while waiting for responses.
public class TempCarparkView extends AppCompatActivity implements UraDBController.URACallback {
   /**
    * This is the interface defining the callback from the coordinate converter
    * @author Chin Han Wen
    */
    interface ConversionCallbacks{
        public void getConverted(double[] result, String name);
    }

    /**
     * This is the implementation of the returnParking function from the callback interface URACallback
     * It sends in each individual name-coordinate pair to the coordinate converter function.
     * @param names The list of all carpark names
     * @param coordinates The list of all carpark coordinates
     * @author Chin Han Wen
     */
    @Override
    public void returnParking(List<String> names, List<String> coordinates) {
        size = names.size();
        for(int a  = 0; a < size; a++){
            String[] non_converted = coordinates.get(a).split(",");
            converter(non_converted[0], non_converted[1], names.get(a));
        }
    }

    private RecyclerView carparkRecView;
    private CarparkRecViewAdapter adapter;
    private ArrayList<Carpark> carparks = new ArrayList<>();
    private Context current = this;
    private ConversionCallbacks conversion_callback;
    private ArrayList<Carpark> copy;

    private UraDBController ura_db_controller;
    private OkHttpClient http_client;
    private int size;
    private ReentrantLock lock = new ReentrantLock();

    /**
     * This function implements the carpark list user interface.
     *
     * @author Chin Han Wen
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp_carpark_view);
        GoogleApiAvailability checker = new GoogleApiAvailability();
        //Defines the method called when a converted carparks coordinate is received from converter().
        //Uses a simple lock to guarantee that only 1 thread can modify the list at any point.
        this.conversion_callback = new ConversionCallbacks(){
            @Override
            public void getConverted(double[] result, String name){
                lock.lock();
                try{
                    carparks.add(new Carpark(new LatLng(result[0], result[1]), name));
                    System.out.println(carparks.size());
                    Collections.sort(carparks);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setCarparks(carparks);
                        }
                    });

                }
                finally{
                    lock.unlock();
                }
            }
        };

        if(checker.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS){
            http_client = new OkHttpClient.Builder().callTimeout(120, TimeUnit.SECONDS).build();
            ura_db_controller = new UraDBController();
            ura_db_controller.setUracallback(this);
            String accessKey = "14977109-00e5-40fc-911d-8979d93db584";
            String token = "ye-7R-NGSzE+69yN3wB185x-883QU3Z4eVqK9RXn1hte08mKT5w+17uT09G7mfd7Y9hy+TGy7B34e7-d7ghXfR8b56WaWf817U10";
            ura_db_controller.accessUraDB(accessKey, token);
            carparkRecView = findViewById(R.id.carparkRecView);
            adapter = new CarparkRecViewAdapter(current);
            carparkRecView.setAdapter(adapter);
            carparkRecView.setLayoutManager(new LinearLayoutManager(current));
        }
        else{
            Toast.makeText(this, "This app cannot run unless Google Play Services are installed.", Toast.LENGTH_LONG).show();
            this.finish();
            System.exit(0);
        }
    }

    /**
     * This function converts coordinates from SVY21 format to WGS84 format via a HTML request.
     * The return is sent to a ConversionCallback.
     * @param latitude The SVY21 latitude of the carpark.
     * @param longitude The SVY21 longitude of the carpark.
     * @param name The name of the corresponding carpark.
     * @author Chin Han Wen
     */
    private void converter(String latitude, String longitude, String name) {
        double[] converted = {0, 0};
        String url = "https://developers.onemap.sg/commonapi/convert/3414to4326";
        HttpUrl.Builder urlbuilder = HttpUrl.parse(url).newBuilder();
        urlbuilder.addQueryParameter("X", latitude);
        urlbuilder.addQueryParameter("Y", longitude);
        String built_url = urlbuilder.build().toString();
        Request request = new Request.Builder()
                .url(built_url)
                .build();
        http_client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {e.printStackTrace();}

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String myres = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(myres);
                        // extract coords
                        String latitude = jsonObject.getString("latitude");
                        String longitude = jsonObject.getString("longitude");
                        converted[0] = Double.parseDouble(latitude);
                        converted[1] = Double.parseDouble(longitude);
                        conversion_callback.getConverted(converted, name);
                    }catch(JSONException e){
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
}



