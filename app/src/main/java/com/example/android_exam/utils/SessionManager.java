package com.example.android_exam.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.android_exam.App;
import com.example.android_exam.data.api.ApiManager;
import com.example.android_exam.data.api.AuthCallback;
import com.example.android_exam.data.dto.response.ApiResponse;
import com.example.android_exam.data.dto.user.UserProfileDto;
import com.example.android_exam.data.models.base.User;
import com.google.gson.Gson;

public class SessionManager {
    public interface UserCallback {
        void onUserLoaded(User user);
        void onError(String error);
    }
    private static final String PREF_NAME = "FoodAppSession";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER = "user_data";
    private static final SharedPreferences sharedPreferences =
            App.getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    private static final Gson gson = new Gson();
    public interface LoginCheckCallback {
        void onResult(boolean success, String errorMessage);
    }

    public static void checkLoginStatus(Context context, LoginCheckCallback callback) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);
        String token = prefs.getString("token", null);

        if (isLoggedIn && token != null&& !token.isEmpty()) {

            //call API to validate token
            ApiManager.getInstance().getAuthClient().validateToken(token, new AuthCallback<ApiResponse<UserProfileDto>>() {
                @Override
                public void onSuccess(ApiResponse<UserProfileDto> response) {
                    if (response.isSuccess()) {
                        ApiManager.getInstance().setAuthToken(token);
                        User currentUser = response.getData().toUser();
                        saveUser(currentUser);
                        callback.onResult(true, "Đăng nhập thành công.");
                    } else {
                        callback.onResult(false, "Đăng nhập không thành công, Vui lòng đăng nhập lại.");
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    callback.onResult(false, errorMessage);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    callback.onResult(false, "Lỗi kết nối: " + throwable.getMessage());
                }

            });
        } else {
            callback.onResult(false, "Chưa đăng nhập hoặc thiếu thông tin.");
        }
    }


    public static void saveToken(Context context, String token) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("token", token);
        editor.putBoolean(KEY_IS_LOGGED_IN, token != null && !token.isEmpty());
        editor.apply();
    }

    public static String getToken() {
        return sharedPreferences.getString("token", null);
    }

    public static void clearToken() {
        sharedPreferences.edit().remove("token").apply();
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, false).apply();
        ApiManager.getInstance().setAuthToken(null);
    }

    public static void clearSession() {
        clearUser();
        clearToken();
    }


    public static void saveUser(User user) {
        String json = gson.toJson(user);
        Log.d("SessionManager", "Saving user: " + json);
        sharedPreferences.edit().putString(KEY_USER, json).apply();
    }

    // Lấy User
    public static void getUser(UserCallback callback) {
        String json = sharedPreferences.getString(KEY_USER, null);
        if (json == null) {
            //Call Api to get user data if not found in SharedPreferences
            String token = getToken();
            if (token != null && !token.isEmpty()) {
                ApiManager.getInstance().getAuthClient().getUserProfile(new AuthCallback<ApiResponse<UserProfileDto>>() {
                    @Override
                    public void onSuccess(ApiResponse<UserProfileDto> result) {
                        //Log full result for debugging
                        Log.d("SessionManager", "Fetched user profile: " + result.getData().toJson());
                        var user = result.getData().toUser();
                        Log.d("SessionManager", "User profile fetched successfully: " + user.toJson());
                        saveUser(user);
                        if (callback != null) {
                            callback.onUserLoaded(user);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        // Handle error if needed
                        // For example, you can log the error or show a message to the user
                        System.err.println("Error fetching user profile: " + error);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        // Handle failure if needed
                        // For example, you can log the throwable or show a message to the user
                        System.err.println("Failure fetching user profile: " + throwable.getMessage());
                    }
                });
            }
        }
        else {
            User user = gson.fromJson(json, User.class);
            callback.onUserLoaded(user);
        }
    }

    // Xóa User (Logout)
    public static void clearUser() {
        sharedPreferences.edit().remove(KEY_USER).apply();
    }


}