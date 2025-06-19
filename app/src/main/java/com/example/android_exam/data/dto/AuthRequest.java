package com.example.android_exam.data.dto;

public class AuthRequest {
    public String username;
    public String password;
    public String email;

    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public AuthRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}