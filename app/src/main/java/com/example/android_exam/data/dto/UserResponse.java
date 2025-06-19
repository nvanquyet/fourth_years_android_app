package com.example.android_exam.data.dto;

import com.example.android_exam.data.models.User;

import java.util.List;

public class UserResponse {
    public boolean success;
    public String message;
    public User data;
    public List<String> errors;
}