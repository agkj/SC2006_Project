package com.example.sc2006_project.boundary;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sc2006_project.CarparkLotDisplay;
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
import java.util.Arrays;
import java.util.List;

import com.example.sc2006_project.entity.Carpark;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class TempCarparkView extends AppCompatActivity{
    private RecyclerView carparkRecView;
    private ArrayList<Carpark> carparks = new ArrayList<>();

    private TextView mTextViewResult;
    String[] coordinateArray;
    String[] ppNameArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp_carpark_view);

        OkHttpClient client = new OkHttpClient();

        String url = "https://www.ura.gov.sg/uraDataService/invokeUraDS?service=Car_Park_Details";


        //changes daily, generate own accessKey and token
        String accessKey = "ed2ed5ec-8a5e-47ab-ae19-a14d963c707c";
        String token = "@a59dPSRcb943dg2evykDepcaewY9tCqeb8Bad-Wu1t5C8BmS76dnd183ZQ4321R7kBe2euT@1ZZ-xXe9c2E3Rexas-ar7J4bFd4";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("AccessKey", accessKey).addHeader("Token",token)
                .build();

        client.newCall(request).enqueue(new Callback() {
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
                        List<String> ppNames = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject temp = jsonArray.getJSONObject(i);
                            String ppName = temp.getString("ppName");
                            ppNames.add(ppName);
                        }
                        ppNameArray = ppNames.toArray(new String[0]);

                        // extract the coordinates of parking lot
                        JSONArray jsonArray1 = new JSONArray(result);
                        JSONObject result1 = jsonArray1.getJSONObject(0);
                        JSONArray geometriesArray = result1.getJSONArray("geometries");
                        JSONObject coordinatesObject = geometriesArray.getJSONObject(0);
//                        String coordinates = coordinatesObject.getString("coordinates");
                        List<String> coordinates = new ArrayList<>();
                        for (int j = 0; j < geometriesArray.length(); j++) {
                            JSONObject temp = geometriesArray.getJSONObject(j);
                            String coordinate = temp.getString("coordinates");
                            coordinates.add(coordinate);
                        }
                        coordinateArray = coordinates.toArray(new String[0]);
//                        System.out.println(coordinateArray);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
//                    String test = jsonObject.getString();
                    TempCarparkView.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            mTextViewResult.setText(myResponse);
                        }
                    });
                }
            }
        });

//        System.out.println(ppNameArray[0]);

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
            //test2.add("L1");
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
            carparks.add(new Carpark(new LatLng(1.54527,113.683411),ppNameArray[0]));
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
