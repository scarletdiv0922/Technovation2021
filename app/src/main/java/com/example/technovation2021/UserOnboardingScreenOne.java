package com.example.technovation2021;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class UserOnboardingScreenOne extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_onboarding_screen_one);
    }

    public void skipClicked(View view) {
        Intent intent = new Intent(UserOnboardingScreenOne.this, CalendarActivity.class);
        startActivity(intent);
    }

    public void next1Clicked(View view) {
        Intent intent = new Intent(UserOnboardingScreenOne.this, UserOnboardingScreenTwo.class);
        startActivity(intent);
    }
}