package com.example.sc2006_project.boundary;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.navigation.fragment.NavHostFragment;

import com.example.sc2006_project.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

/**
 *
 * This class implements the homepage interface, it displays other functions such as editing of profile
 * viewing reservations, making reservations, view a list of carparks and logging out.
 *
 * @author Goh Kai Jun, Alger
 *
 */
public class Homepage extends AppCompatActivity {

    private TextView btnEditProfile, btnViewReservation,btnViewCarPark, btnCurrentReservation,btnLogout;

    private FirebaseAuth auth, fAuth;
    private TextView userEmail, userName, userPhone, userCarPlate;
    private FirebaseUser user;
    private FirebaseFirestore fStore;
    private String userID;


    /**
     * This function initializes the homepage activity.
     *
     * @author Goh Kai Jun, Alger
     *  */


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        btnEditProfile = findViewById(R.id.edit_profile);
        btnViewCarPark = findViewById(R.id.view_car_park);
        btnViewReservation = findViewById(R.id.view_reservation);
        btnCurrentReservation = findViewById(R.id.current_reservation);
        btnLogout = findViewById(R.id.logout);

        auth = FirebaseAuth.getInstance();
        userEmail = findViewById(R.id.user_email);
        userName = findViewById(R.id.user_name);
        userPhone = findViewById(R.id.user_phone);
        userCarPlate = findViewById(R.id.user_carplate);

        user = auth.getCurrentUser();

        //new
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("users").document(userID);

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {

            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {

                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                    // userName.setText(documentSnapshot.getString("name"));
                    userName.setText(documentSnapshot.getString("name"));
                    userEmail.setText(documentSnapshot.getString("email"));
                    userPhone.setText(documentSnapshot.getString("phone"));
                    userCarPlate.setText(documentSnapshot.getString("carPlate"));

                }
            });


        }

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditProfile.class);
                startActivity(intent);
                finish();
            }
        });


        btnViewCarPark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TempCarparkView.class);
                startActivity(intent);
            }
        });

        btnCurrentReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CurrentReservation.class);
                startActivity(intent);
            }
        });

        btnViewReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ViewReservation.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                //FirebaseAuth.getInstance().signOut();
                finish();
            }
        });


    }
}
