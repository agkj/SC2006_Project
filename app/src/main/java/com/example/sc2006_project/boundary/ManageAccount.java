package com.example.sc2006_project.boundary;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sc2006_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ManageAccount extends AppCompatActivity {



    FirebaseAuth auth, fAuth;;
    Button button;
    TextView userEmail, userName, userPhone;
    FirebaseUser user;

    FirebaseFirestore fStore;

    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account) ;

        //old
        auth = FirebaseAuth.getInstance();
        userEmail = findViewById(R.id.user_email);
        userName = findViewById(R.id.user_name);
        userPhone = findViewById(R.id.user_phone);
        button = findViewById(R.id.logout);
        user = auth.getCurrentUser();
        //old

        //new
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);

        //new

//        if(user ==null){
//            Intent intent = new Intent(getApplicationContext(), Login.class);
//            startActivity(intent);
//            finish();
//        }else{
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {

                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                    // userName.setText(documentSnapshot.getString("name"));
                    userName.setText(documentSnapshot.getString("name"));
                    userEmail.setText(documentSnapshot.getString("email"));
                    userPhone.setText(documentSnapshot.getString("phone"));

                }
            });


    //    }


        //signs user out
        button.setOnClickListener(new View.OnClickListener() {
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