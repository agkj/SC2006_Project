package com.example.sc2006_project.boundary;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sc2006_project.R;
import com.example.sc2006_project.control.AccountRegister;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;


public class EditProfile extends AppCompatActivity {

    private FirebaseFirestore fStore;
    private EditText nameEditText, phoneNumberEditText, carplateEditText;
    private Button saveButton;

    private FirebaseAuth auth, fAuth;
    private FirebaseUser user;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        nameEditText = findViewById(R.id.nameEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        carplateEditText = findViewById(R.id.carPlateEditText);
        saveButton = findViewById(R.id.saveButton);


        nameEditText = findViewById(R.id.nameEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        carplateEditText = findViewById(R.id.carPlateEditText);

        saveButton = findViewById(R.id.saveButton);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();


        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {

            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // Get the user data as a Map
                    Map<String, Object> userData = documentSnapshot.getData();

                    // Set the EditTexts to the current values in the document
                    String name = userData.get("name").toString();
                    String phone = userData.get("phone").toString();
                    String carPlate = userData.get("carPlate").toString();

                    nameEditText.setText(name);
                    phoneNumberEditText.setText(phone);
                    carplateEditText.setText(carPlate);
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String newName = nameEditText.getText().toString();
                String newPhone = phoneNumberEditText.getText().toString();
                String newCarPlate = carplateEditText.getText().toString();

                if (TextUtils.isEmpty(newName) || TextUtils.isEmpty(newPhone) || TextUtils.isEmpty(newCarPlate)) {

                    Toast.makeText(EditProfile.this, "Empty fields, try again.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!(newPhone.replaceAll("\\s", "").matches("^[89]\\d{7}$"))) {
                    phoneNumberEditText.setError("Invalid Singapore phone number format");
                    phoneNumberEditText.requestFocus();
                    //Toast.makeText(AccountRegister.this, "Phone number must be 8 digits", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (    !(newCarPlate.startsWith("S"))
                        || !(Character.isLetter(newCarPlate.charAt(1)))
                        || !(Character.isLetter(newCarPlate.charAt(2)))
                        || !(Character.isLetter(newCarPlate.charAt(7)))

                        || Character.isLetter(newCarPlate.charAt(3))
                        || Character.isLetter(newCarPlate.charAt(4))
                        || Character.isLetter(newCarPlate.charAt(5))
                        || Character.isLetter(newCarPlate.charAt(6))
                        || newCarPlate.length() !=8 ) {

                    carplateEditText.setError("Invalid car plate");
                    carplateEditText.requestFocus();
                    // Toast.makeText(AccountRegister.this, "Invalid car plate number", Toast.LENGTH_SHORT).show();
                    return;
                }


                Map<String, Object> newUser = new HashMap<>();
                newUser.put("name", newName);
                newUser.put("phone", newPhone);
                newUser.put("carPlate", newCarPlate);

                fStore.collection("users").document(userID).set(newUser, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Display a message indicating that the user information was updated
                                Toast.makeText(EditProfile.this, "Relogin to update.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Display an error message if the update fails
                                Toast.makeText(EditProfile.this, "Error updating user information", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}




