package com.example.android_exam.activities;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Init loading manager với context này
        LoadingActivity.init(this);
    }

    @Override
    protected void onDestroy() {
        LoadingActivity.getInstance().hide();
        super.onDestroy();
    }

    // Chỉ cần 2 method này thôi!
    protected void showLoading() {
        LoadingActivity.getInstance().show(this);
    }

    protected void hideLoading() {
        LoadingActivity.getInstance().hide();
    }
}
