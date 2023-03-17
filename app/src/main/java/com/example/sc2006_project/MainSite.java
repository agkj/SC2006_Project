package com.example.sc2006_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.navigation.fragment.NavHostFragment;

import com.example.sc2006_project.boundary.TempCarparkView;
import com.google.firebase.auth.FirebaseAuth;

public class MainSite extends AppCompatActivity {

    private TextView btnViewCarPark;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_site);
        btnViewCarPark = findViewById(R.id.view_car_park);

        btnViewCarPark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), TempCarparkView.class);
                startActivity(intent);
            }
        });
    }
}
