package com.example.technovation2021;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity {

    ProgressBar pbar;
    private static final String LOG_TAG = SignUp.class.getSimpleName();
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        pbar = findViewById(R.id.idProgressBarSignUp);
        pbar.setVisibility(View.GONE);
    }

    public void signup_clicked(View view) {
        EditText email = findViewById(R.id.userEmailSignUpScreen);
        EditText pswd = findViewById(R.id.idPasswordSignUpScreen);
        EditText confirmPassword = findViewById(R.id.idConfirmPasswordSignUpScreen);


        if (TextUtils.isEmpty(email.getText().toString()) ) {
            Toast.makeText(SignUp.this, "Email cannot be empty.",
                    Toast.LENGTH_SHORT).show();
            email.setError("Email cannot be empty.");
            return;
        }

        if ( !Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            Toast.makeText(SignUp.this, "Email address is not valid.",
                    Toast.LENGTH_SHORT).show();
            email.setError("Email address is not valid.");
            return;
        }

        if ( TextUtils.isEmpty(pswd.getText().toString()) ) {
            Toast.makeText(SignUp.this, "Password is empty.",
                    Toast.LENGTH_SHORT).show();
            pswd.setError("Password cannot be empty.");
            return;
        }

        if ( pswd.length() < 6 ) {
            Toast.makeText(SignUp.this, "Password is too short.",
                    Toast.LENGTH_SHORT).show();
            pswd.setError("Too short. Must be minimum 6 characters.");
            return;
        }

        if ( TextUtils.isEmpty(confirmPassword.getText().toString()) ) {
            Toast.makeText(SignUp.this, "Password is empty.",
                    Toast.LENGTH_SHORT).show();
            confirmPassword.setError("Password cannot be empty.");
            return;
        }

        if ( !pswd.getText().toString().equals(confirmPassword.getText().toString()) ) {
            confirmPassword.setError("Passwords do not match.");
            return;
        }



        Log.d( LOG_TAG, "go ahead authenticate the user");
            Toast.makeText(SignUp.this, "All good. Sign up the user!",
                        Toast.LENGTH_SHORT).show();
            pbar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email.getText().toString(), pswd.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if ( task.isSuccessful()) {
                        Toast.makeText(SignUp.this, "User signed up!", Toast.LENGTH_SHORT).show();
                        pbar.setVisibility(View.GONE);
                        Intent intent = new Intent(SignUp.this, UserInfo.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(SignUp.this, "User already exists. Please go back and log in.", Toast.LENGTH_SHORT).show();
                        Log.d(LOG_TAG, task.getException().toString());
                        pbar.setVisibility(View.GONE);
                    }
                }
            });
    }


    public void CancelClicked(View view){
        Intent intent = new Intent(SignUp.this, MainActivity.class);
        startActivity(intent);
    }

}