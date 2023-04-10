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

    /**
     * This function sets the callback to the given callback instance used in a different class.
     * @param callback The callback instance referenced.
     * @author Chin Han Wen
     */
    public void setUracallback(URACallback callback){
        this.Uracallback = callback;
    }

    /**
     * This function requests the URA database with Access Key and Token and store the json data into
     * a string variable, and extract the name and coordination of car park lots into two string arrays.
     * @param accessKey
     * @param token
     * @author He Haoshen
     */
    public void accessUraDB(String accessKey, String token){
        OkHttpClient client = new OkHttpClient();
        OkHttpClient client2 = new OkHttpClient();

        String url = "https://www.ura.gov.sg/uraDataService/invokeUraDS?service=Car_Park_Details";
        String lotsURL = "https://www.ura.gov.sg/uraDataService/invokeUraDS?service=Car_Park_Availability";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("AccessKey", accessKey).addHeader("Token",token)
                .build();

        Request request1 = new Request.Builder()
                .url(lotsURL)
                .addHeader("AccessKey", accessKey).addHeader("Token",token)
                .build();
        System.out.println(request1);

        List<String> names = new ArrayList<>();
        List<String> coordinates = new ArrayList<>();
        List<String> numbers = new ArrayList<>();
        List<String> lots = new ArrayList<>();

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

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject temp = jsonArray.getJSONObject(i);
                            if(temp.getString("vehCat").equals("Car")){
                                if(!names.contains(temp.getString("ppName"))){
                                    String ppName = temp.getString("ppName");
                                    names.add(ppName);
                                }
                            }
                        }

                        // extract the coordinates of parking lot
                        JSONArray jsonArray1 = new JSONArray(result);

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

                        JSONArray jsonArray2 = new JSONArray(result);
                        for (int k = 0; k < jsonArray2.length(); k++) {
                            JSONObject temp = jsonArray2.getJSONObject(k);
                            if(temp.getString("vehCat").equals("Car")){
//                                }
                                String ppCodes = temp.getString("ppCode");
                                if(names.contains(temp.getString("ppName")) && !numbers.contains(ppCodes)){
                                    numbers.add(ppCodes);
                                }

                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                client2.newCall(request1).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {e.printStackTrace();}

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String myResponse = response.body().string();
                            try {
                                JSONObject jsonObject = new JSONObject(myResponse);
                                String result = jsonObject.getString("Result");
                                JSONArray jsonArray = new JSONArray(result);
                                for (int i = 0; i < numbers.size(); i++) {
                                    int j;
                                    for(j = 0; j < jsonArray.length(); j++){
                                        JSONObject temp = jsonArray.getJSONObject(j);
                                        if(numbers.get(i).equals(temp.getString("carparkNo"))){
                                            lots.add(temp.getString("lotsAvailable"));
                                            break;
                                        }
                                    }
                                    if(j>=jsonArray.length()){
                                        lots.add("Unknown");
                                    }
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        System.out.println(lots);
                        Uracallback.returnParking(names, coordinates, lots);
                    }
                });
            }
        });
        System.out.println(names);
    }

    /**
     * This is the interface defining the callback from the URA database.
     * @author Chin Han Wen
     */
    public interface URACallback{
        void returnParking(List<String> names, List<String> coordinates, List<String> lots);
    }

}