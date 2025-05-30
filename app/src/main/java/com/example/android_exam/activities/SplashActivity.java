package com.example.android_exam.activities;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android_exam.R;
import com.example.android_exam.activities.LoginActivity;
import com.example.android_exam.activities.HomeActivity;
import com.example.android_exam.utils.SessionManager;
import com.example.android_exam.data.local.database.AppDatabase;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize database
        AppDatabase.initialize(this);

        // Check login status after splash delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            checkLoginStatus();
        }, SPLASH_DELAY);
    }

    private void checkLoginStatus() {
        if (SessionManager.isLoggedIn(this)) {
            // User is logged in, go to main activity
            startActivity(new Intent(this, HomeActivity.class));
        } else {
            // User is not logged in, go to login activity
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }
}
