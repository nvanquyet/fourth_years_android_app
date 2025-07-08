package com.example.android_exam.data.api;

public interface ResponseCallback<T> {
    void onSuccess(T result);
    void onError(String error);
    void onFailure(Throwable throwable);
}

