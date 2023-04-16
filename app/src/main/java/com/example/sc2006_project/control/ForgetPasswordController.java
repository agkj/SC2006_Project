package com.example.sc2006_project.control;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.sc2006_project.R;
import com.example.sc2006_project.boundary.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordController extends AppCompatActivity {


    private EditText emailEditText;

    private Button resetPasswordButton;
    private ProgressBar progressBar;


    private FirebaseAuth fAuth;

    /**
     * This function initializes the forget password activity
     *
     * @author Goh Kai Jun, Alger
     *  */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        emailEditText = findViewById(R.id.reset_email);
        resetPasswordButton = findViewById(R.id.reset_password_button);
        //progressBar = findViewById(R.id.progress_bar);

        fAuth = FirebaseAuth.getInstance();


        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });

    }


    /**
     * This function implements the reset password functionality.
     *
     * It takes in the user email input and checks if it exists in Firebase.
     * @author Goh Kai Jun, Alger
     *  */

    private void resetPassword() {

        String email = emailEditText.getText().toString().trim();

        if (email.isEmpty()) {

            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            emailEditText.setError("Please provide a valid email");
            emailEditText.requestFocus();
            return;

        }

       // progressBar.setVisibility(View.VISIBLE);

        fAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ForgetPasswordController.this, "Please check your email to reset your password", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ForgetPasswordController.this, "Try again, something wrong has happened or account does not exists", Toast.LENGTH_SHORT).show();
                }
            }
        });

      //  progressBar.setVisibility(View.INVISIBLE);


    }


}