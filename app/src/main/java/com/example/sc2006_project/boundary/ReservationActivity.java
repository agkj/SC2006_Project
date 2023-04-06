package com.example.sc2006_project.boundary;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sc2006_project.R;
import com.example.sc2006_project.entity.Reservation;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReservationActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore fStore;
    private String userID;
    private TimePicker timePicker;
    private RadioGroup timeModeSelector;
    private RadioButton startTimeButton;
    private RadioButton endTimeButton;
    private int startHour, startMinute, endHour, endMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_reservation);

        FirebaseApp.initializeApp(getApplicationContext());
        timePicker = findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);
        timeModeSelector = findViewById(R.id.time_mode_selector);
        startTimeButton = findViewById(R.id.start_time_button);
        endTimeButton = findViewById(R.id.end_time_button);
        Button saveButton = findViewById(R.id.make_reservation);

        RadioButton startTimeButton = findViewById(R.id.start_time_button);
        TextView startTimeTextView = findViewById(R.id.start_time_text_view);
        RadioButton endTimeButton = findViewById(R.id.end_time_button);
        TextView endTimeTextView = findViewById(R.id.end_time_text_view);
        TimePicker timePicker = findViewById(R.id.time_picker);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if (startTimeButton.isChecked()) {
                    getTimeString(hourOfDay, minute, startTimeTextView);
                    startHour = timePicker.getHour();
                    startMinute = timePicker.getMinute();
                } else if (endTimeButton.isChecked()) {
                    getTimeString(hourOfDay, minute, endTimeTextView);
                    endHour = timePicker.getHour();
                    endMinute = timePicker.getMinute();
                }
            }
        });

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://parkersc2006-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference reservationsRef = database.getReference("reservations");

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentTimeMillis = System.currentTimeMillis();
                Query ongoingReservationQuery = reservationsRef.orderByChild("startTimeMillis").endAt(currentTimeMillis);

                ongoingReservationQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // if ongoing reservation exists
                            Toast.makeText(ReservationActivity.this, "There is an ongoing reservation", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            if (startTimeTextView.getText().toString().equals("Selected Start Time") || endTimeTextView.getText().toString().equals("Selected End Time")) {
                                Toast.makeText(ReservationActivity.this, "Please select both start and end time", Toast.LENGTH_SHORT).show();
                                return; // Exit the method to prevent saving invalid reservation data
                            }
                            // Get the selected start and end times from the TextView elements and the user ID from firestore; parking lot passed from previous activity
                            String startTime = startTimeTextView.getText().toString();
                            String endTime = endTimeTextView.getText().toString();
                            String userID = documentReference.getId();

                            // Check if the end time is after the start time
                            if (endHour < startHour || (endHour == startHour && endMinute <= startMinute)) {
                                // End time is earlier than start time, so display an error message and return
                                Toast.makeText(ReservationActivity.this, "End time must be after start time", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            //parking lot variable needs to be passed on from previous activity, which is view map
                            String parkingLot = "parkingLot";

                            // Create a Reservation object to store the start and end times
                            Reservation reservation = new Reservation(startTime, endTime, userID, parkingLot);

                            // Write the Reservation object to the database
                            reservationsRef.push().setValue(reservation);

                            // Show a Toast message to indicate that the reservation has been saved
                            Toast.makeText(ReservationActivity.this, "Reservation saved successfully!", Toast.LENGTH_SHORT).show();

                            //opens navigation page
                            //Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                            //startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle the error
                    }
                });
            }
        });
    }


    private void getTimeString(int hourOfDay, int minute, TextView textView) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        Locale singaporeLocale = new Locale("en", "SG");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", singaporeLocale);
        String timeString = format.format(calendar.getTime());
        textView.setText(timeString);
    }
}
