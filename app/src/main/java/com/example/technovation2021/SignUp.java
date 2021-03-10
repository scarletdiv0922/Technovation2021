package com.example.technovation2021;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

public class SignUp extends AppCompatActivity {

    ProgressBar pbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        pbar = findViewById(R.id.idProgressBarSignUp);
        pbar.setVisibility(View.GONE);
    }
}