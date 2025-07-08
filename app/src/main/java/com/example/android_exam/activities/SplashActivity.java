package com.example.android_exam.activities;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android_exam.R;
import com.example.android_exam.module.image.ImagePickerHelper;
import com.example.android_exam.utils.SessionManager;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LoadingActivity.init(this);
        ImagePickerHelper.initialize(this);

        setContentView(R.layout.activity_splash);
        // Check login status after splash delay
        new Handler(Looper.getMainLooper()).postDelayed(this::checkLoginStatus, SPLASH_DELAY);
    }

    private void checkLoginStatus() {
        SessionManager.checkLoginStatus(this, new SessionManager.LoginCheckCallback() {
            @Override
            public void onResult(boolean success, String errorMessage) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ImagePickerHelper.handlePermissionResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImagePickerHelper.cleanup(this);
    }
}
