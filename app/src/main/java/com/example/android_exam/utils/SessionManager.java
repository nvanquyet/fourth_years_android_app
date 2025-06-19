package com.example.android_exam.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.android_exam.data.models.User;
import com.example.android_exam.data.remote.DataRepository;
import com.example.android_exam.data.remote.RemoteDataRepository;

public class SessionManager {
    private static final String PREF_NAME = "FoodAppSession";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public interface LoginCheckCallback {
        void onResult(boolean success, User user, String errorMessage);
    }

    // Kiểm tra trạng thái đăng nhập
    public static void checkLoginStatus(Context context, LoginCheckCallback callback) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);
        String token = prefs.getString("token", null); // Lấy token từ SharedPreferences

        if (isLoggedIn && token != null) {
            // Gọi API /api/auth/me qua RemoteDataRepository
            RemoteDataRepository.getInstance(context).getUserInformation(token, new DataRepository.AuthCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    currentUser = user;
                    callback.onResult(true, user, null);
                }

                @Override
                public void onError(String error) {
                    clearSession(context);
                    currentUser = null;
                    callback.onResult(false, null, "Phiên đăng nhập không hợp lệ: " + error);
                }
            });
        } else {
            currentUser = null;
            callback.onResult(false, null, "Chưa đăng nhập hoặc thiếu thông tin.");
        }
    }

    public static void saveUserSession(Context context, User user, String token) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString("token", token);
        editor.apply();
        currentUser = user;
    }

    public static void clearSession(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        currentUser = null;
    }

    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString("token", null); // Lấy token từ SharedPreferences
    }
}