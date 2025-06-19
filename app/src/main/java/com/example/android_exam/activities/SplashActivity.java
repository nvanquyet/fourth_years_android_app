package com.example.android_exam.activities;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android_exam.R;
import com.example.android_exam.data.local.entity.User;
import com.example.android_exam.utils.SessionManager;
import com.example.android_exam.data.local.database.AppDatabase;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize database
        LocalDataRepository.getInstance(this);

        // Check login status after splash delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            checkLoginStatus();
        }, SPLASH_DELAY);
    }

    private void checkLoginStatus() {
        SessionManager.checkLoginStatus(this, new SessionManager.LoginCheckCallback() {
            @Override
            public void onResult(boolean success, User user, String errorMessage) {
                if(success){
                    // User is logged in, go to main activity
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                } else {
                    // User is not logged in, go to login activity
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }

                finish();
            }
        });

    }
}
