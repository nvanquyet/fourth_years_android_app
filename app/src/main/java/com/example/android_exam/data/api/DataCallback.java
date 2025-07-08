package com.example.android_exam.data.api;

public interface DataCallback<T> extends ResponseCallback<T> {
    default void onLoading(boolean isLoading) {
        // Optional: Handle loading state
    }
}
