package com.example.sc2006_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

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

    private TextView mTextViewResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_carpark_lot_display);

        // get the string array from the URA database
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
                        String[] ppNameArray = ppNames.toArray(new String[0]);

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
                        String[] coordinateArray = coordinates.toArray(new String[0]);
                        System.out.println(coordinateArray);

                        mTextViewResult.setText(coordinateArray[0]);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
//                    String test = jsonObject.getString();
                    CarparkLotDisplay.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTextViewResult.setText(myResponse);
                        }
                    });
                }
            }
        });

        carparklotrecview = findViewById(R.id.carparkLotRecView);
        lots.add(new CarparkLot(new LatLng(13,10),"Name 1111"));
        lots.add(new CarparkLot(new LatLng(11,12), "Name 2222"));

        CarparkLotRecViewAdapter adapter = new CarparkLotRecViewAdapter(this, CarparkLotDisplay.this);
        adapter.setLots(lots);

        carparklotrecview.setAdapter(adapter);
        carparklotrecview.setLayoutManager(new LinearLayoutManager(this));
    }
}
