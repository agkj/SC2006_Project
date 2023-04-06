package com.example.sc2006_project.boundary;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.SearchView;
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
import java.util.List;
import java.util.Locale;

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

import android.widget.SearchView;


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
        for(int a  = 0; a < 50; a++){
            String[] non_converted = coordinates.get(a).split(",");
            converter(non_converted[0], non_converted[1], names.get(a));
        }
    }

    private RecyclerView carparkRecView;
    private CarparkRecViewAdapter adapter;
    private ArrayList<Carpark> carparks = new ArrayList<>();
    private Context current = this;
    private ConversionCallbacks conversion_callback;

    private UraDBController ura_db_controller;

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
        this.conversion_callback = new ConversionCallbacks(){
            @Override
            public void getConverted(double[] result, String name){
                carparks.add(new Carpark(new LatLng(result[0], result[1]), name));
//                ArrayList<String> asset_list = new ArrayList<>();
//                asset_list.add("carpark_ntu_c");
//                asset_list.add("bonk");
//                ArrayList<String> level_list = new ArrayList<>();
//                level_list.add("L1");
//                level_list.add("L2");
//                level_list.add("L3");
//                ArrayList<String> test1 = new ArrayList<>();
//                test1.add("carpark_ntu_f");
//                ArrayList<String> test2 = new ArrayList<>();
//                //test2.add("L1");
//                carparks.add(new Carpark(new LatLng(1.345275,103.683411),"Carpark Q/Student Services Centre"));
//                carparks.add(new Carpark(
//                        new LatLng(1.346776,103.683368),
//                        new LatLngBounds(new LatLng(1.34635, 103.68311), new LatLng(1.34787, 103.6837)),
//                        "Carpark F/Mat Sci Carpark",
//                        test1,
//                        test2));
//                carparks.add(new Carpark(
//                        new LatLng(1.345720,103.681330),
//                        new LatLngBounds(new LatLng(1.3455,103.68096),new LatLng(1.346,103.68234)),
//                        "Carpark C/Some Rando",
//                        asset_list,
//                        level_list));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setCarparks(carparks);
                    }
                });
            }
        };

        SearchView simpleSearchView = (SearchView) findViewById(R.id.simpleSearchView);
        ArrayList<Carpark> carparkList = carparks;
        simpleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // This method will be called when the user submits their search query
                // The 'query' parameter contains the user's input
                String userInput = query;
//                        System.out.println(carparkList);
                int i = 0;
                for(i=0; i<carparkList.size(); i++)
                {
                    Carpark c = carparkList.get(i);
                    String name = c.getLocation_name().trim();
                    System.out.println(name);
                    String upper = userInput.toUpperCase();
                    if(name.equals(upper)) {
                        break;
                    }
                }
                ArrayList<Carpark> searchResult = new ArrayList<>();
                searchResult.add(carparkList.get(i));
//                        System.out.println(i);
                adapter.setCarparks(searchResult);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // This method will be called when the user changes the search query text
                // You can use this to update search suggestions or dynamically filter results
                return true;
            }
        });

        if(checker.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS){
            ura_db_controller = new UraDBController();
            ura_db_controller.setUracallback(this);
            String accessKey = "ed2ed5ec-8a5e-47ab-ae19-a14d963c707c";
            String token = "HeS4S-e4E3J32PSZU+9JcuW-wVbvBURv721k1mWcaK1aj8867dYG819yks7wNe8-MF8Ke5X3Ra45wa3BC99d43q47G5aqd8Y5BSx";
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
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
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



