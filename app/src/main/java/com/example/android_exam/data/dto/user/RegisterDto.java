package com.example.android_exam.data.dto.user;

import com.google.gson.annotations.SerializedName;

public class RegisterDto {
    @SerializedName("username")
    private String username = "";
    @SerializedName("email")
    private String email = "";
    @SerializedName("password")
    private String password = "";
    @SerializedName("firstName")
    private String firstName = "";
    @SerializedName("lastName")
    private String lastName = "";

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}
