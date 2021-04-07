//First Screen: Welcome and Login/Sign Up
package com.example.technovation2021;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
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
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Declare objects
    Button loginButton;
    Button signUpButton;
    ProgressBar pbar;
    //ArrayList<itemModel> arrayList;
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

            tv.setText("techtest@gmail.com");
            pswd.setText("techtest");
        }

        // When the Login and Sign Up buttons are clicked, take the user to CalendarActivity
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email = findViewById(R.id.userEmail);
                EditText pswd = findViewById(R.id.userPassword);

                // Do sanity checks to make sure data is good!
                if (TextUtils.isEmpty(email.getText().toString()) ) {
                    //Toast.makeText(MainActivity.this, "Email cannot be empty.",
                            //Toast.LENGTH_SHORT).show();
                    email.setError("Email cannot be empty.");
                    return;
                }
                if ( !Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
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
                // TODO: disable login button if a login is already in progress? first login is taking quite a bit of time.
                // TODO: check on firebase authentication private key configuring in app.
                mAuth.signInWithEmailAndPassword(email.getText().toString(), pswd.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if ( task.isSuccessful() ) {
                            pbar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                            startActivity(intent);
                        }
                        else {
                            //Toast.makeText(MainActivity.this, "User has not signed up.", Toast.LENGTH_SHORT).show();
                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                            switch (errorCode) {
                                case "ERROR_USER_NOT_FOUND":
                                    //email.setError("User not registered!");
                                    Toast.makeText(MainActivity.this, "User is not registered.", Toast.LENGTH_LONG).show();
                                    break;
                                case "ERROR_WRONG_PASSWORD":
                                    Toast.makeText(MainActivity.this, "Incorrect password.", Toast.LENGTH_LONG).show();
                                    break;
                            }
                            pbar.setVisibility(View.GONE);
                        }
                        //TODO: Figure out if pw is wrong or no account is made
                    }
                });
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void forgotPasswordClicked(View view) {
//        Intent intent = new Intent(MainActivity.this, UserInfo.class);
//        startActivity(intent);

        /*
        FirebaseRealtimeDatabase frd = new FirebaseRealtimeDatabase();
        frd.getAllActivities("activityList");
         */

        /*
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = sharedPref.edit();
        String sluser = sharedPref.getString("sluser", "invalid");
        String slpswd = sharedPref.getString("slpswd", "invalid");
        String subdomain = sharedPref.getString("slsubdomain", "hjh-fusd-ca");
        //Log.d(LOG_TAG, sluser+slpswd+subdomain);
        new HTTPReqTask(sluser, slpswd, subdomain).execute();
         */
    }
}
