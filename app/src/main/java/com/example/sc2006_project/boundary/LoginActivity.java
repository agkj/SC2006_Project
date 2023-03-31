package com.example.sc2006_project.boundary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sc2006_project.R;
import com.example.sc2006_project.control.AccountRegister;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editTextEmail, editTextPassword;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private TextView registerNow, forgotPassword;

    @Override
    public void onStart() {
        super.onStart();


        // Check if user is signed in, if signed in, go into main page
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), Homepage.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * This function implements the application login functionality
     *
     * @author Goh Kai Jun, Alger
     *  */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.emailLogin);
        editTextPassword = findViewById(R.id.passwordLogin);
        btnLogin = findViewById(R.id.login_button);
        registerNow = findViewById(R.id.registerNow);

        forgotPassword = findViewById(R.id.forgot_password);

        //forget password
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),ForgetPassword.class);
                startActivity(intent);
                finish();


            }
        });

        //registration
        registerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AccountRegister.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String email, password;

                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){

                    Toast.makeText(LoginActivity.this, "Empty fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    // Log.d(TAG, "signInWithEmail:success");
                                    //FirebaseUser user = mAuth.getCurrentUser();
                                    // updateUI(user);
                                    if(mAuth.getCurrentUser().isEmailVerified()){
                                        Toast.makeText(LoginActivity.this, "Welcome",
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), Homepage.class);
                                        startActivity(intent);
                                        finish();

                                    }
                                    else{
                                        Toast.makeText(LoginActivity.this, "Please verify your account",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    // If sign in fails, display a message to the user.
                                    // Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Account does not exist.",
                                            Toast.LENGTH_SHORT).show();
                                    // updateUI(null);
                                }
                            }
                        });

            }
        });

    }
}