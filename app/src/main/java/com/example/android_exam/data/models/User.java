package com.example.android_exam.data.models;

import com.example.android_exam.data.models.enums.NutritionGoal;

public class User {
    public int id;
    public String username;
    public String email;
    public String firstName;
    public String lastName;

    public NutritionGoal nutritionGoal;

    public boolean isActive;
    public String lastLoginAt;
    public String createdAt;
}