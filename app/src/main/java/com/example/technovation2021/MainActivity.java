//First Screen: Welcome and Login/Sign Up
package com.example.technovation2021;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Declare objects
    Button loginButton;
    Button signUpButton;
    ProgressBar pbar;
    // References (firebase signin): https://www.youtube.com/watch?v=TwHmrZxiPA8

    // TODO: Add progress bar icon
    // TODO: Change "Welcome!" string font

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // TODO: make validating email and password a method by itself
    // TODO: Make custom Toast() message using BuildConfig.DEBUG

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize objects with elements from xml files
        loginButton = findViewById(R.id.login_button);
        signUpButton = findViewById(R.id.sign_up_button);
        pbar = findViewById(R.id.progressBar);

        pbar.setVisibility(View.GONE);
        if ( BuildConfig.DEBUG ) {
            EditText tv = findViewById(R.id.userEmail);
            EditText pswd = findViewById(R.id.userPassword);

            tv.setText("test@test.com");
            pswd.setText("test123");
        }

        // When the Login and Sign Up buttons are clicked, take the user to CalendarActivity
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText tv = findViewById(R.id.userEmail);
                EditText pswd = findViewById(R.id.userPassword);
                if (TextUtils.isEmpty(tv.getText().toString()) ) {
                    Toast.makeText(MainActivity.this, "Email cannot be empty.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if ( !Patterns.EMAIL_ADDRESS.matcher(tv.getText().toString()).matches()) {
                    // TODO: Change these toast message to field.setError()
                    Toast.makeText(MainActivity.this, "Email address is not valid.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if ( TextUtils.isEmpty(pswd.getText().toString()) ) {
                    Toast.makeText(MainActivity.this, "Password is empty.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // TODO: Make password length a constant variable
                if ( pswd.length() < 5 ) {
                    Toast.makeText(MainActivity.this, "Password is too short.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                pbar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(tv.getText().toString(), pswd.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if ( task.isSuccessful() ) {
                            pbar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "User has not signed up.", Toast.LENGTH_SHORT).show();
                            pbar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                //startActivity(intent);
                EditText tv = findViewById(R.id.userEmail);
                EditText pswd = findViewById(R.id.userPassword);

                if (TextUtils.isEmpty(tv.getText().toString()) ) {
                    Toast.makeText(MainActivity.this, "Email cannot be empty.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if ( !Patterns.EMAIL_ADDRESS.matcher(tv.getText().toString()).matches()) {
                    Toast.makeText(MainActivity.this, "Email address is not valid.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if ( TextUtils.isEmpty(pswd.getText().toString()) ) {
                    Toast.makeText(MainActivity.this, "Password is empty.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if ( pswd.length() < 5 ) {
                    Toast.makeText(MainActivity.this, "Password is too short.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d( LOG_TAG, "go ahead authenticate the user");
                pbar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(tv.getText().toString(), pswd.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if ( task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "User registered!", Toast.LENGTH_SHORT).show();
                            pbar.setVisibility(View.GONE);
                            Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "User already exists. Logging in.", Toast.LENGTH_SHORT).show();
                            pbar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }
}