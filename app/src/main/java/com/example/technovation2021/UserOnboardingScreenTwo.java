package com.example.technovation2021;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class UserOnboardingScreenTwo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_onboarding_screen_two);
    }

    public void next2Clicked(View view) {
        Intent intent = new Intent(UserOnboardingScreenTwo.this, UserOnboardingScreenThree.class);
        startActivity(intent);
    }
}