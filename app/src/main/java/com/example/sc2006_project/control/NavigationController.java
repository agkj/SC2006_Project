package com.example.sc2006_project.control;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;


public class NavigationController extends AppCompatActivity {
    private Context current;
    /**
     * Navigation class controller constructor.
     * @param context Takes in the context of the activity the controller was created in.
     * @author Chin Han Wen
     */
    public NavigationController(Context context) {
        this.current = context;
    }

    /**
     * This function checks if Google Maps is installed on the current device.
     * @return <code>true</code> if the device has Google Maps installed;
     *         <code?false</code> otherwise.
     * @author Chin Han Wen
     */
    public boolean check_gmaps_install() {
        PackageManager pm = current.getPackageManager();
        try {
            pm.getPackageInfo("com.google.android.apps.maps", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            System.out.println("Nop");
            return false;
        }
    }

    /**
     * This function takes in a carpark location and launches Google Maps navigation to that location.
     * @param destination The WGS84 coordinates of the carpark.
     * @author Chin Han Wen
     */
    public void navigate(String destination) {
        Uri uri = Uri.parse("google.navigation:q=" + destination);
        Intent launch_gmaps = new Intent(Intent.ACTION_VIEW, uri);
        launch_gmaps.setPackage("com.google.android.apps.maps");
        launch_gmaps.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        current.startActivity(launch_gmaps);
    }

    /**
     * This function takes in WGS84 latitude and longitudes in double separately and containiates them into a single String return.
     * Useful for converting separate, double coordinate pairs to a single String.
     * @param latitude The latitude of a carpark location.
     * @param longitude The longitude of a carpakr location.
     * @return A String concatenating both inputs in the format latitude,longitude.
     */
    public String location_to_string(double latitude, double longitude) {
        String out = "";
        out = out + latitude + ",";
        out = out + longitude;
        return out;
    }
}
