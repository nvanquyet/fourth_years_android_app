package com.example.android_exam.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "users", indices = {@Index(value = {"id"})})
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String username;

    @NonNull
    public String password;

    public User() {
        username = "guest";
        password = "123456";
    }

    // Getters and setters
    @NonNull
    public String getUsername() { return username; }
    public void setUsername(@NonNull String username) { this.username = username.isEmpty() ? "guest" : username; }

    @NonNull
    public String getPassword() { return password; }
    public void setPassword(@NonNull String password) { this.password = password.isEmpty() ? "123456" : password; }
}