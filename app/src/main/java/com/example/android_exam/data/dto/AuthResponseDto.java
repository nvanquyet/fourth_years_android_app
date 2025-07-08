package com.example.android_exam.data.dto;
import com.example.android_exam.data.dto.user.UserDto;
import com.example.android_exam.data.dto.user.UserProfileDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class AuthResponseDto {
    @SerializedName("token")
    private String token = "";
    @SerializedName("expiresAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'", timezone = "UTC")
    private Date expiresAt;
    @SerializedName("user")
    private UserProfileDto user = new UserProfileDto();

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Date getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Date expiresAt) { this.expiresAt = expiresAt; }
    public UserProfileDto getUser() { return user; }
    public void setUser(UserProfileDto user) { this.user = user; }
}