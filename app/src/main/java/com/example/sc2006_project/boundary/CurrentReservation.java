package com.example.sc2006_project.boundary;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sc2006_project.R;
import com.example.sc2006_project.entity.Reservation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CurrentReservation extends AppCompatActivity {

    private TextView ongoingReservationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_reservation);

        ongoingReservationTextView = findViewById(R.id.ongoingReservation);

        long currentTimeMillis = System.currentTimeMillis();

        // Query the reservations node in the database for ongoing reservations
        DatabaseReference reservationsRef = FirebaseDatabase.getInstance("https://parkersc2006-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("reservations");
        Query ongoingReservationQuery = reservationsRef.orderByChild("startTimeMillis").endAt(currentTimeMillis).limitToLast(1);

        ongoingReservationQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Get the ongoing reservation data from the current snapshot
                        String startTime = snapshot.child("startTime").getValue(String.class);
                        String endTime = snapshot.child("endTime").getValue(String.class);
                        String parkingLot = snapshot.child("parkingLot").getValue(String.class);
                        // Update the TextView field with the current reservation information
                        String currentReservationInfo = "Current Reservation: " + startTime + " to " + endTime + " at " + parkingLot;
                        ongoingReservationTextView.setText(currentReservationInfo);
                        return;
                    }
                }
                ongoingReservationTextView.setText("No ongoing reservations");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}

