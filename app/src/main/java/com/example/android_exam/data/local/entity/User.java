package com.example.android_exam.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String username;

    @NonNull
    public String password; // giả lập login local

    public User() {
        username = "";
        password = "123456";
    }
}