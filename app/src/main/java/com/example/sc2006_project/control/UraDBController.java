package com.example.sc2006_project.control;

import android.content.Context;
import android.net.ipsec.ike.TunnelModeChildSessionParams;

import androidx.annotation.NonNull;

import com.example.sc2006_project.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UraDBController {
    Context context;
    URACallback Uracallback;

    public UraDBController() {
    }

    public void setUracallback(URACallback callback){
        this.Uracallback = callback;
    }

    public void accessUraDB(String accessKey, String token){
        OkHttpClient client = new OkHttpClient();

        String url = "https://www.ura.gov.sg/uraDataService/invokeUraDS?service=Car_Park_Details";

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
                                if(!names.contains(temp.getString("ppName"))){
                                    String ppName = temp.getString("ppName");
                                    names.add(ppName);
                                }
                            }
                        }
                        //String[] ppNameArray = names.toArray(new String[0]);

                        // extract the coordinates of parking lot
                        JSONArray jsonArray1 = new JSONArray(result);
//                            JSONObject result1 = jsonArray1.getJSONObject(0);
//                            JSONArray geometriesArray = result1.getJSONArray("geometries");
//                            JSONObject coordinatesObject = geometriesArray.getJSONObject(0);
//                            String coordinates = coordinatesObject.getString("coordinates");
                        List<String> to_remove = new ArrayList<>();
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            JSONObject temp = jsonArray1.getJSONObject(j);
                            if(temp.getString("vehCat").equals("Car")){
                                JSONArray geometries = temp.getJSONArray("geometries");
                                if(geometries.length() == 0){
                                    to_remove.add(temp.getString("ppName"));
                                    continue;
                                }
                                JSONObject first_coords = geometries.getJSONObject(0);
                                String str_coordinates = first_coords.getString("coordinates");
                                if(names.contains(temp.getString("ppName")) && !coordinates.contains(str_coordinates)){
                                    coordinates.add(str_coordinates);
                                }
                            }
                        }
                        for(int i = 0; i < to_remove.size() ; i++){
                            names.remove(to_remove.get(i));
                        }

                        System.out.println(names.size());
                        System.out.println(coordinates.size());
                        Uracallback.returnParking(names, coordinates);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }


    public interface URACallback{
        void returnParking(List<String> names, List<String> coordinates);
    }

}