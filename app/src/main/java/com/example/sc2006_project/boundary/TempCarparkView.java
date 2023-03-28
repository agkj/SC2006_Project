package com.example.sc2006_project.boundary;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sc2006_project.boundary.CarparkLotDisplay;
import com.example.sc2006_project.R;
import com.example.sc2006_project.control.CarparkRecViewAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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


public class TempCarparkView extends AppCompatActivity{

    interface ConversionCallbacks{
        public void getConverted(double[] result, String name);
    }
    interface URACallbacks{
        public void getCarparks(List<String> parking_lots, List<String> ura_coordinates);
    }
    private RecyclerView carparkRecView;
    private CarparkRecViewAdapter adapter;
    private List<String> ppNames = new ArrayList<>();
    private List<String> coords = new ArrayList<>();
    private ArrayList<Carpark> carparks = new ArrayList<>();
    private Context current = this;
    private ConversionCallbacks conversion_callback;
    private URACallbacks ura_callback;
    private double[] test;
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
        this.ura_callback = new URACallbacks() {
            @Override
            public void getCarparks(List<String> parking_lots, List<String> ura_coordinates) {
                for(int a  = 0; a < 50; a++){
                    String[] non_converted = ura_coordinates.get(a).split(",");
                    converter(non_converted[0], non_converted[1], parking_lots.get(a));
                }
            }
        };
        if(checker.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS){
            // get the string array from the URA database
            OkHttpClient client = new OkHttpClient();

            String url = "https://www.ura.gov.sg/uraDataService/invokeUraDS?service=Car_Park_Details";


            //changes daily, generate own accessKey and token
            String accessKey = "14977109-00e5-40fc-911d-8979d93db584";
            String token = "fr4D090VYf9JXX1-69H4R14wamcAv8j5K9KcknFF8mF9k18bhQy@09yc710yG78fcbU-1+0fJ0b9tz89GuruSf85um9J-b9312th";

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("AccessKey", accessKey).addHeader("Token",token)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                List<String> names = new ArrayList<>();
                List<String> coordinates = new ArrayList<>();
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {e.printStackTrace();}

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String myResponse = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(myResponse);

                            // extract the name of the parking lots
                            String result = jsonObject.getString("Result");
                            JSONArray jsonArray = new JSONArray(result);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject temp = jsonArray.getJSONObject(i);
                                if(temp.getString("vehCat").equals("Car")){
                                    String ppName = temp.getString("ppName");
                                    names.add(ppName);
                                }
                            }
                            //String[] ppNameArray = names.toArray(new String[0]);

                            // extract the coordinates of parking lot
                            JSONArray jsonArray1 = new JSONArray(result);
//                            JSONObject result1 = jsonArray1.getJSONObject(0);
//                            JSONArray geometriesArray = result1.getJSONArray("geometries");
//                            JSONObject coordinatesObject = geometriesArray.getJSONObject(0);
//                        String coordinates = coordinatesObject.getString("coordinates");
                            List<Integer> to_remove = new ArrayList<>();
                            for (int j = 0; j < jsonArray1.length(); j++) {
                                JSONObject temp = jsonArray1.getJSONObject(j);
                                if(temp.getString("vehCat").equals("Car")){
                                    JSONArray geometries = temp.getJSONArray("geometries");
                                    if(geometries.length() == 0){
                                        to_remove.add(j);
                                        continue;
                                    }
                                    JSONObject first_coords = geometries.getJSONObject(0);
                                    String str_coordinates = first_coords.getString("coordinates");
                                    coordinates.add(str_coordinates);
                                }
                            }
                            for(int i = to_remove.size()-1; i > 0; i--){
                                names.remove(to_remove.get(i).intValue());
                            }
                            for(int i = 0; i < 50;i++){
                                System.out.println(names.get(i));
                                System.out.println(coordinates.get(i));
                            }
                            System.out.println(names.size());
                            System.out.println(coordinates.size());
                            ura_callback.getCarparks(names, coordinates);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
//            for (int i = 0; i < coordinates.size(); i++){
//                String[] str_cur_coords = coordinates.get(i).split(",");
//                double[] cur_coords = converter(str_cur_coords[0], str_cur_coords[1]);
//                carparks.add(new Carpark(new LatLng(cur_coords[0], cur_coords[1]), ppNames.get(i)));
//            }
            //converter(Double.toString(31063.017),Double.toString(31665.0933));
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
                        System.out.println(converted[0]);
                        System.out.println(converted[1]);
                        conversion_callback.getConverted(converted, name);
                    }catch(JSONException e){
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
}



