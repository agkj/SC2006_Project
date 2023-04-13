package com.example.sc2006_project.control;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sc2006_project.R;
import com.example.sc2006_project.boundary.HomepageActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class CurrentReservationController extends AppCompatActivity {

    private TextView startTimeTextView, endTimeTextView, destinationTextView;
    private Context current = this;
    private double latitude, longitude;
    private String endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_reservation);

        startTimeTextView = findViewById(R.id.start_time);
        endTimeTextView = findViewById(R.id.end_time);
        destinationTextView = findViewById(R.id.destination);

        TimeZone timeZone = TimeZone.getTimeZone("Asia/Singapore");
        Calendar calendar = Calendar.getInstance(timeZone);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        dateFormat.setTimeZone(timeZone);
        String currentDate = dateFormat.format(calendar.getTime());

        // Query the reservations node in the database for ongoing reservations
        DatabaseReference reservationsRef = FirebaseDatabase.getInstance("https://parkersc2006-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("reservations");
        Query ongoingReservationQuery = reservationsRef.orderByChild("endTime").startAt(currentDate);

        ongoingReservationQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Get the ongoing reservation data from the current snapshot
                        String startTime = snapshot.child("startTime").getValue(String.class);
                        endTime = snapshot.child("endTime").getValue(String.class);
                        String parkingLot = snapshot.child("parkingLot").getValue(String.class);
                        // Need for navigation
                        latitude = snapshot.child("lat").getValue(double.class);
                        longitude = snapshot.child("long").getValue(double.class);
                        // Update the TextView field with the current reservation information
                        startTimeTextView.setText(startTime);
                        endTimeTextView.setText(endTime);
                        destinationTextView.setText(parkingLot);
                        return;
                    }
                } else {
                    startTimeTextView.setText("No ongoing reservations");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        Button button_navigate = findViewById(R.id.navigate);
        button_navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startTimeTextView.getText() != "No ongoing reservations") {
                    NavigationController nav_controller = new NavigationController(current);
                    boolean gmaps_installed = nav_controller.check_gmaps_install();
                    if (!gmaps_installed) {
                        Toast.makeText(current, "Please install Google Maps to utilise this function", Toast.LENGTH_SHORT).show();
                    } else {
                        String location = nav_controller.location_to_string(latitude, longitude);
                        System.out.println(location);
                        nav_controller.navigate(location);
                    }
                } else {
                    Toast.makeText(CurrentReservationController.this, "You can't navigate without a reservation", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button button_cancel = findViewById(R.id.cancel);

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startTimeTextView.getText() != "No ongoing reservations") {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CurrentReservationController.this);
                    builder.setMessage("Are you sure you want to cancel your reservation?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Query query = reservationsRef.orderByChild("endTime").equalTo(endTime);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot reservationSnapshot : dataSnapshot.getChildren()) {
                                        reservationSnapshot.getRef().removeValue(); // Remove the value from the Realtime Database
                                        AlertDialog.Builder builder = new AlertDialog.Builder(CurrentReservationController.this);
                                        builder.setMessage("Success!");
                                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent(getApplicationContext(), HomepageActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) { }
                            });

                            Intent intent = new Intent(getApplicationContext(), HomepageActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("No", null);
                    builder.show();
                } else {
                    Toast.makeText(CurrentReservationController.this, "You can't cancel a reservation if you don't have one", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button button_back = findViewById(R.id.back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomepageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}


