package com.example.sc2006_project.control;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sc2006_project.R;
import com.example.sc2006_project.boundary.HomepageActivity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewReservationController extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore fStore;
    private String userID;

    /**
     * This function implements the view reservation user interface.
     *
     * @author Chi Seo Hyeon
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reservation);

        ListView listView = findViewById(R.id.listview);

        FirebaseApp.initializeApp(getApplicationContext());
        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);

        String userID = documentReference.getId();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://parkersc2006-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        Query query = databaseReference.child("reservations").orderByChild("userID").equalTo(userID);

        /**
         This method sets a ValueEventListener on a Firebase database reference, which retrieves data from the database and populates a list with the retrieved data.
         @param query The Firebase database reference to retrieve data from
         @param listView The ListView to populate with retrieved data
         @author Chi Seo Hyeon
         */

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Map<String, String>> dataList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Map<String, String> dataMap = new HashMap<>();
                    dataMap.put("column1", dataSnapshot.child("startTime").getValue(String.class));
                    dataMap.put("column2", dataSnapshot.child("endTime").getValue(String.class));
                    dataMap.put("column3", dataSnapshot.child("parkingLot").getValue(String.class));
                    dataList.add(dataMap);
                }

                String[] from = {"column1", "column2", "column3"};
                int[] to = {R.id.column1_textview, R.id.column2_textview, R.id.column3_textview};
                SimpleAdapter adapter = new SimpleAdapter(ViewReservationController.this, dataList, R.layout.reservation_lot, from, to);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors here
            }
        });

        Button button_back = findViewById(R.id.back_button);
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
