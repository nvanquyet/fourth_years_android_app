package com.example.android_exam.data.dto;

import com.example.android_exam.data.models.User;

import java.util.List;

public class AuthResponse {
    public boolean success;
    public String message;
    public AuthData data;
    public List<String> errors;

    public static class AuthData {
        public String token;
        public String expiresAt;
        public User user;
    }
}
