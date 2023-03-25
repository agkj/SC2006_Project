package com.example.sc2006_project.boundary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
//import androidx.navigation.fragment.NavHostFragment;

import com.example.sc2006_project.R;

import com.example.sc2006_project.boundary.MapActivity;
import com.example.sc2006_project.boundary.TempCarparkView;
import com.google.firebase.auth.FirebaseAuth;

public class Homepage extends AppCompatActivity {

    private TextView btnViewCarPark;
    private TextView btnEditProfile;

    private TextView btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_site);

        btnViewCarPark = findViewById(R.id.view_car_park);
        btnEditProfile = findViewById(R.id.edit_profile);
        btnLogout = findViewById(R.id.logout);

        btnViewCarPark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TempCarparkView.class);
                startActivity(intent);
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ManageAccount.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });







    }
}
