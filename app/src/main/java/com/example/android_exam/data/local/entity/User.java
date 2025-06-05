package com.example.android_exam.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "users", indices = {@Index(value = {"username"}, unique = true)})
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String username;

    @NonNull
    private String hashedPassword; // Lưu mật khẩu đã mã hóa

    public double weight; // Cân nặng (kg) - hỗ trợ tính toán dinh dưỡng
    public double height; // Chiều cao (cm)
    public int age; // Tuổi
    public String gender; // Giới tính: "male", "female", "other"

    public User() {
        username = "guest";
        hashedPassword = "hashed_123456"; // Giá trị mặc định, nên mã hóa
        weight = 0.0;
        height = 0.0;
        age = 0;
        gender = "other";
    }

    @NonNull
    public String getUsername() { return username; }
    public void setUsername(@NonNull String username) { this.username = username.isEmpty() ? "guest" : username; }

    @NonNull
    public String getHashedPassword() { return hashedPassword; }
    public void setHashedPassword(@NonNull String hashedPassword) {
        this.hashedPassword = hashedPassword.isEmpty() ? "hashed_123456" : hashedPassword;
    }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender.isEmpty() ? "other" : gender; }
}