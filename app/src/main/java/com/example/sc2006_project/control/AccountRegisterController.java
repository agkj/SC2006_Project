package com.example.sc2006_project.control;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sc2006_project.boundary.LoginActivity;
import com.example.sc2006_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AccountRegisterController extends AppCompatActivity {

    private TextInputEditText editTextEmail, editTextPassword, editTextPhone, editTextName, editTextCarPlate;
    private Button btnReg;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private String userID;



    /**
     * This function initializes the account registration page
     *
     * @author Goh Kai Jun, Alger
     */

    @Override
    public void onStart() {
        super.onStart();

    }

    /**
     * This function initializes the account register activity
     *
     * It checks for valid user input details such as, Email, Name, Password, Phone, Car Plate and Password.
     * Successful inputs will generate a verification email which will be sent to the user email before logging in.
     *
     * @author Goh Kai Jun, Alger
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);


        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.emailRegister);
        editTextPassword = findViewById(R.id.passwordRegister);
        btnReg = findViewById(R.id.btn_register);


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

                //check empty fields
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(carPlate)) {

                    Toast.makeText(AccountRegisterController.this, "Empty fields, try again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //check name validity must have no numbers
                if (name.matches(".*\\d+.*")) {
                    editTextName.setError("Name must not contain numbers.");
                    editTextName.requestFocus();
                    return;
                }


                //check email validity
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextEmail.setError("Invalid email format");
                    editTextEmail.requestFocus();
                    //Toast.makeText(AccountRegister.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                    return;
                }

                //check singapore phone
                if (!(phone.replaceAll("\\s", "").matches("^[89]\\d{7}$"))) {
                    editTextPhone.setError("Invalid Singapore phone number format");
                    editTextPhone.requestFocus();
                    //Toast.makeText(AccountRegister.this, "Phone number must be 8 digits", Toast.LENGTH_SHORT).show();
                    return;
                }

                //check carplate format eg: SAB1234D
                if (    !(carPlate.startsWith("S"))
                        || !(Character.isLetter(carPlate.charAt(1)))
                        || !(Character.isLetter(carPlate.charAt(2)))
                        || !(Character.isLetter(carPlate.charAt(7)))

                        || Character.isLetter(carPlate.charAt(3))
                        || Character.isLetter(carPlate.charAt(4))
                        || Character.isLetter(carPlate.charAt(5))
                        || Character.isLetter(carPlate.charAt(6))
                        || carPlate.length() !=8 ) {

                    editTextCarPlate.setError("Invalid car plate");
                    editTextCarPlate.requestFocus();
                    // Toast.makeText(AccountRegister.this, "Invalid car plate number", Toast.LENGTH_SHORT).show();
                    return;
                }

                //check password length
                if (!(password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.{6,}).+$"))) {
                    editTextPassword.setError("Password must contain at least 6 characters, one upper and lowercase and one number");
                    editTextPassword.requestFocus();
                    // Toast.makeText(AccountRegister.this, "Password must contain at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }


                //upon successful checks


                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "createUserWithEmail:success");


                            //send email verification
                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        userID = mAuth.getCurrentUser().getUid();

                                        //user object to record and store data into firestore
                                        DocumentReference documentReference = fStore.collection("users").document(userID);
                                        Map<String, Object> user = new HashMap<>();
                                        user.put("email", email);
                                        user.put("name", name);
                                        user.put("phone", phone);
                                        user.put("carPlate", carPlate);
                                        user.put("password", password);
                                        // user.put("password", password);
                                        documentReference.set(user);

                                        Toast.makeText(AccountRegisterController.this, "Please verify your email address before logging in.", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        Toast.makeText(AccountRegisterController.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();


                                    }
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(AccountRegisterController.this, "Account creation failed. Try again", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        });

        //go back to login page after creating account


    }
}