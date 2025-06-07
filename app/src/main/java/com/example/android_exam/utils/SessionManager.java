package com.example.android_exam.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.android_exam.data.local.entity.User;
import com.example.android_exam.data.remote.LocalDataRepository;

public class SessionManager {

    private static final String PREF_NAME = "FoodAppSession";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    public interface LoginCheckCallback {
        void onResult(boolean success, User user, String errorMessage);
    }

    public static void saveUserSession(Context context, int userId, String username, String password) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password); // Lưu mật khẩu nếu cần xác thực lại
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public static int getCurrentUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_USER_ID, -1);
    }

    public static String getCurrentUsername(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USERNAME, "");
    }

    public static String getCurrentPassword(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_PASSWORD, "");
    }

    public static void checkLoginStatus(Context context, LoginCheckCallback callback) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);
        String username = prefs.getString(KEY_USERNAME, "");
        String password = prefs.getString(KEY_PASSWORD, "");

        if (isLoggedIn && !username.isEmpty() && !password.isEmpty()) {
            // Gọi lại login từ repository
            LocalDataRepository.getInstance().login(username, password, new LocalDataRepository.AuthCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    callback.onResult(true, user, null);
                }

                @Override
                public void onError(String error) {
                    callback.onResult(false, null, error);
                }
            });
        } else {
            callback.onResult(false, null, "Chưa đăng nhập hoặc thiếu thông tin.");
        }
    }

    public static void clearSession(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}
