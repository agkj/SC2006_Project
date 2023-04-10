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


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ReservationActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore fStore;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_reservation);

        FirebaseApp.initializeApp(getApplicationContext());
        TimePicker timePicker = findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);

        RadioButton oneHour = findViewById(R.id.one_button);
        RadioButton twoHour = findViewById(R.id.two_button);
        RadioButton threeHour = findViewById(R.id.three_button);

        TextView startTimeTextView = findViewById(R.id.start_time_text_view);
        TextView endTimeTextView = findViewById(R.id.end_time_text_view);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                getTimeString(hourOfDay, minute, startTimeTextView);
            }
        });

        RadioGroup radioGroup = findViewById(R.id.time_selector);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                Calendar cal = Calendar.getInstance();
                Calendar clone = Calendar.getInstance();
                if (radioButton != null && radioButton.isChecked()) {
                    SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String startTime = startTimeTextView.getText().toString();
                    try {
                        Date restime = time.parse(startTime);
                        cal.setTime(restime);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    if(oneHour.isChecked()){
                        clone = cal;
                        clone.add(Calendar.HOUR_OF_DAY, 1);
                        endTimeTextView.setText(time.format(clone.getTime()));
                    }else if(twoHour.isChecked()){
                        clone = cal;
                        clone.add(Calendar.HOUR_OF_DAY, 2);
                        endTimeTextView.setText(time.format(clone.getTime()));
                    }else if(threeHour.isChecked()){
                        clone = cal;
                        clone.add(Calendar.HOUR_OF_DAY, 3);
                        endTimeTextView.setText(time.format(clone.getTime()));
                    }
                }
            }
        });


        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://parkersc2006-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference reservationsRef = database.getReference("reservations");

        Button reserveButton = findViewById(R.id.reserve_button);
        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startTimeTextView.getText().toString().equals("Selected Start Time")) {
                    Toast.makeText(ReservationActivity.this, "Please select start time", Toast.LENGTH_SHORT).show();
                    return; // Exit the method to prevent saving invalid reservation data
                } if (!oneHour.isChecked() && !twoHour.isChecked() && !threeHour.isChecked()) {
                    Toast.makeText(ReservationActivity.this, "Please select reservation duration", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    TimeZone timeZone = TimeZone.getTimeZone("Asia/Singapore");
                    Calendar calendar = Calendar.getInstance(timeZone);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    dateFormat.setTimeZone(timeZone);
                    String currentDate = dateFormat.format(calendar.getTime());
                    Query ongoingReservationQuery = reservationsRef.orderByChild("endTime").startAt(currentDate);
                    // Query the reservations node in the database for ongoing reservations
                    ongoingReservationQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // if ongoing reservation exists
                                Toast.makeText(ReservationActivity.this, "There is an ongoing reservation", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            // Get the selected start and end times from the TextView elements and the user ID from firestore; parking lot passed from previous activity
                            String startTime = startTimeTextView.getText().toString();
                            String endTime = endTimeTextView.getText().toString();
                            String userID = documentReference.getId();
                            double latitude = MapActivity.carpark_loc_pub.latitude;
                            double longitude = MapActivity.carpark_loc_pub.longitude;

                            String parkingLot = TempCarparkView.lotName;

                            // Create a Reservation object to store the start and end times
                            Reservation reservation = new Reservation(startTime, endTime, userID, parkingLot, latitude, longitude);

                            // Write the Reservation object to the database
                            reservationsRef.push().setValue(reservation);

                            // Show a Toast message to indicate that the reservation has been saved
                            Toast.makeText(ReservationActivity.this, "Reservation saved successfully!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplicationContext(), Homepage.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle the error
                        }
                    });
                }
            }
        });
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TempCarparkView.class);
                startActivity(intent);
            }
        });
    }
    private void getTimeString ( int hourOfDay, int minute, TextView textView){
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Singapore");
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String timeString = format.format(calendar.getTime());
        textView.setText(timeString);
    }
}
