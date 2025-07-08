package com.example.android_exam.data.dto.user;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordDto {
    @SerializedName("currentPassword")
    private String currentPassword = "";
    @SerializedName("newPassword")
    private String newPassword = "";
    @SerializedName("confirmNewPassword")
    private String confirmNewPassword = "";

    // Getters and Setters
    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    public String getConfirmNewPassword() { return confirmNewPassword; }
    public void setConfirmNewPassword(String confirmNewPassword) { this.confirmNewPassword = confirmNewPassword; }
}
