package com.example.sc2006_project.control;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;


public class NavigationController extends AppCompatActivity {
    private Context current;

    public NavigationController(Context context) {
        this.current = context;
    }

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

    public void navigate(String destination) {
        Uri uri = Uri.parse("google.navigation:q=" + destination);
        Intent launch_gmaps = new Intent(Intent.ACTION_VIEW, uri);
        launch_gmaps.setPackage("com.google.android.apps.maps");
        launch_gmaps.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        current.startActivity(launch_gmaps);
    }

    public String location_to_string(double latitude, double longitude) {
        String out = "";
        out = out + latitude + ",";
        out = out + longitude;
        return out;
    }
}
