package com.example.android_exam.data.dto.user;

import com.google.gson.annotations.SerializedName;

public class LoginDto {
    @SerializedName("username")
    private String username = "";
    @SerializedName("password")
    private String password = "";

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
