package com.example.android_exam.activities;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.example.android_exam.R;

public class LoadingActivity {
    private static LoadingActivity instance;
    private Dialog loadingDialog;
    private static Context appContext;

    public static LoadingActivity getInstance() {
        if (instance == null) {
            instance = new LoadingActivity();
        }
        return instance;
    }

    // Gọi method này trong Application class hoặc Activity đầu tiên
    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    // Method show - tự động dùng context đã init
    public void show() {
        show(appContext);
    }

    // Method show với context tùy chọn
    public void show(Context context) {
        if (context == null) return;

        hide(); // Đảm bảo không có dialog nào khác

        try {
            loadingDialog = new Dialog(context, R.style.SplashTheme);
            loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
            loadingDialog.setContentView(view);
            loadingDialog.setCancelable(false);
            loadingDialog.setCanceledOnTouchOutside(false);

            if (loadingDialog != null) {
                loadingDialog.show();
            }
        } catch (Exception e) {
            // Ignore
        }
    }

    // Method hide
    public void hide() {
        try {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        } catch (Exception e) {
            // Ignore
        } finally {
            loadingDialog = null;
        }
    }
}
