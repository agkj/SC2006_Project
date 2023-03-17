package com.example.sc2006_project.control;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sc2006_project.Login;
import com.example.sc2006_project.MainActivity;
import com.example.sc2006_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AccountRegister extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword, editTextPhone, editTextName, editTextCarPlate;
    Button btnReg;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    String userID;
    TextView textView;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in, if signed in, go into main page
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);


        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.emailRegister);
        editTextPassword = findViewById(R.id.passwordRegister);
        btnReg = findViewById(R.id.btn_register);
        //textView = findViewById(R.id.loginNow);

        //register additional user details
        editTextPhone = findViewById(R.id.phoneRegister);
        editTextName = findViewById(R.id.nameRegister);
        editTextCarPlate = findViewById(R.id.carplateRegister);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password;
                String name, phone, carPlate;

                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                //register additional user details
                name = String.valueOf(editTextName.getText());
                phone = String.valueOf(editTextPhone.getText());
                carPlate = String.valueOf(editTextCarPlate.getText());



                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){

                    Toast.makeText(AccountRegister.this, "Empty fields, try again.", Toast.LENGTH_SHORT).show();
                    return;

                }
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "createUserWithEmail:success");


                            //email verification
                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        Toast.makeText(AccountRegister.this, "Please verify your email address before logging in.", Toast.LENGTH_SHORT).show();

                                        userID = mAuth.getCurrentUser().getUid();

                                        //user object to record and store data into firestore
                                        DocumentReference documentReference = fStore.collection("users").document(userID);
                                        Map<String,Object> user = new HashMap<>();
                                        user.put("email",email);
                                        user.put("password",password);
                                        user.put("name",name);
                                        user.put("phone",phone);
                                        user.put("carPlate",carPlate);
                                        documentReference.set(user);



                                        Intent intent = new Intent(getApplicationContext(), Login.class);
                                        startActivity(intent);
                                        finish();


                                            }
                                            else{
                                                Toast.makeText(AccountRegister.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();


                                            }
                                        }
                                    });

                                } else {
                                    // If sign in fails, display a message to the user.
                                    //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(AccountRegister.this, "Account creation failed. Try again",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

            }
        });






    }
}