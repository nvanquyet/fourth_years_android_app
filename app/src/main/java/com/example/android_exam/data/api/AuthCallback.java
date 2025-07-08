package com.example.android_exam.data.api;

// Specific callbacks for different operations
public interface AuthCallback<T> extends ResponseCallback<T> {
    default void onTokenExpired() {
        // Optional: Handle token expiration
    }
}
